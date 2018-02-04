/*
 * This file is part of Genesis Mod, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017 Boethie
 * Copyright (c) 2017 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package genesis.util.render

import com.google.common.base.Optional
import com.google.common.collect.HashBiMap
import genesis.GenesisMod
import gnu.trove.map.hash.TIntObjectHashMap
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.resources.IResource
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.ItemModelMesherForge
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.ReflectionHelper
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IRegistryDelegate
import org.apache.commons.lang3.tuple.Pair
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.nio.charset.StandardCharsets
import java.util.*

@SideOnly(Side.CLIENT)
object ModelHelpers {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    private val FAKE_BLOCK: Block = object : BlockAir() {}
    private val LOCATION_TO_FAKE_STATE = HashBiMap.create<ModelResourceLocation, IBlockState>()

    var vanillaModelWrapperModelBlock: Field? = null
    var modelBlockDefinitionMap: Field? = null

    private val FORCED_MODELS: MutableList<Pair<BlockStateContainer, ResourceLocation>> = ArrayList()

    private var _blockDispatcher: BlockRendererDispatcher? = null
    val blockDispatcher: BlockRendererDispatcher
        get() {
            if (_blockDispatcher == null)
                _blockDispatcher = Minecraft.getMinecraft().blockRendererDispatcher

            return _blockDispatcher!!
        }

    private var _blockModelShapes: BlockModelShapes? = null
    val blockModelShapes: BlockModelShapes
        get() {
            if (_blockModelShapes == null)
                _blockModelShapes = modelManager.blockModelShapes

            return _blockModelShapes!!
        }

    private var _blockRenderer: BlockModelRenderer? = null
    val blockRenderer: BlockModelRenderer
        get() {
            if (_blockRenderer == null)
                _blockRenderer = blockDispatcher.blockModelRenderer

            return _blockRenderer!!
        }

    private var _stateToModelLocationMap: Map<IBlockState, ModelResourceLocation>? = null
    val stateToModelLocationMap: Map<IBlockState, ModelResourceLocation>
        get() {
            if (_stateToModelLocationMap == null)
                _stateToModelLocationMap = blockModelShapes.blockStateMapper.putAllStateModelLocations()

            return _stateToModelLocationMap!!
        }

    private var _itemModelLocationMap: Map<IRegistryDelegate<Item>, TIntObjectHashMap<ModelResourceLocation>>? = null
    val itemModelLocations: Map<IRegistryDelegate<Item>, TIntObjectHashMap<ModelResourceLocation>>
        get() {
            if (_itemModelLocationMap == null)
                _itemModelLocationMap = ReflectionHelper.getPrivateValue(ItemModelMesherForge::class.java, modelMesher as ItemModelMesherForge, "locations")

            return _itemModelLocationMap!!
        }

    private var _vanillaModelWrapper: Class<out IModel>? = null
    val vanillaModelWrapper: Class<out IModel>
        get() {
            if (_vanillaModelWrapper == null)
                _vanillaModelWrapper = getModelLoaderClass("VanillaModelWrapper")

            return _vanillaModelWrapper!!
        }

    private var _itemModelDefinitions: Map<IRegistryDelegate<Item>, ItemMeshDefinition>? = null
    val itemModelDefinitions: Map<IRegistryDelegate<Item>, ItemMeshDefinition>
        get() {
            if (_itemModelDefinitions == null)
                _itemModelDefinitions = ReflectionHelper.getPrivateValue(ItemModelMesher::class.java, modelMesher, "shapers", "field_178092_c")

            return _itemModelDefinitions!!
        }

    private var _missingModelLocation: ModelResourceLocation? = null
    val missingModelLocation: ModelResourceLocation
        get() {
            if (_missingModelLocation == null)
                _missingModelLocation = ReflectionHelper.getPrivateValue<ModelResourceLocation, ModelBakery>(ModelBakery::class.java, null, "MODEL_MISSING", "field_177604_a")

            return _missingModelLocation!!
        }

    private var _modelManager: ModelManager? = null
    val modelManager: ModelManager
        get() {
            if (_modelManager == null)
                _modelManager = modelMesher.modelManager

            return _modelManager!!
        }

    private var _modelMesher: ItemModelMesher? = null
    val modelMesher: ItemModelMesher
        get() {
            if (_modelMesher == null)
                _modelMesher = Minecraft.getMinecraft().renderItem.itemModelMesher

            return _modelMesher!!
        }

    fun <T> getModelLoaderClass(name: String): Class<T>? {
        val classes = ModelLoader::class.java.declaredClasses

        return classes.firstOrNull { it.name.endsWith("$" + name) } as Class<T>
    }

    fun getPropertyString(properties: Map<IProperty<*>, Comparable<*>>): String {
        val output = StringBuilder()

        for ((property, value) in properties) {
            if (output.isNotEmpty())
                output.append(",")

            output.append(property.getName() + "=" + value.toString())
        }

        return if (output.isEmpty()) "normal"
        else output.toString()
    }

    fun getPropertyString(state: IBlockState): String {
        return getPropertyString(state.properties)
    }

    fun getLocationWithProperties(loc: ResourceLocation, properties: String): ModelResourceLocation {
        return ModelResourceLocation(loc.resourceDomain + ":" + loc.resourcePath, properties)
    }

    fun isGeneratedItemModel(stack: ItemStack): Boolean {
        return isGeneratedItemModel(getLocationFromStack(stack))
    }

    fun isGeneratedItemModel(loc: ModelResourceLocation): Boolean {
        val missing = missingModelLocation

        if (loc == missing) {
            return false
        }

        val generated = ResourceLocation("minecraft", "builtin/generated")

        var curModel = getModelBlock(loc)

        while (curModel != null) {
            val parent = curModel.parentLocation ?: break

            if (parent == generated) return true

            curModel = getModelBlock(parent)
        }

        return false
    }

    fun bindAtlasTexture() {
        Minecraft.getMinecraft().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    }

    fun renderBakedModel(model: IBakedModel) {
        blockRenderer.renderModelBrightnessColor(model, 1f, 1f, 1f, 1f)
    }

    /**
     * Render the model at the ModelResourceLocation as a plain baked model. Not preferable for rendering in a block.
     */
    fun renderModel(loc: ModelResourceLocation) {
        renderBakedModel(modelManager.getModel(loc))
    }

    /**
     * @return A randomized baked model using the provided block state and position in the world.
     */
    fun getBakedBlockModel(state: IBlockState?, world: IBlockAccess, pos: BlockPos): IBakedModel {
        return if (state == null) modelManager.getModel(missingModelLocation)
        else blockDispatcher.getModelForState(state)
    }

    /**
     * Gets the block state for the model location that was forced to load by this class's dummy block.
     */
    fun getFakeState(location: ModelResourceLocation): IBlockState? = LOCATION_TO_FAKE_STATE[location]

    /**
     * @return A randomized model from a blockstates json forced to load by this helper.
     */
    fun getBakedBlockModel(loc: ModelResourceLocation, world: IBlockAccess, pos: BlockPos): IBakedModel = getBakedBlockModel(getFakeState(loc), world, pos)

    /**
     * @return A duplicate of the original baked model with all faces mapped to the provided sprite,
     * each face projected from its cardinal direction.
     */
    fun getCubeProjectedBakedModel(state: IBlockState, model: IBakedModel, texture: TextureAtlasSprite, pos: BlockPos): IBakedModel {
        return SimpleBakedModel.Builder(state, model, texture, pos).makeBakedModel()
    }

//    /**
//     * @return A duplicate of the original baked model with all faces mapped to the provided sprite,
//     * using their original vertex UVs.
//     */
//    fun getNormalizedCubeProjectedBakedModel(model: IBakedModel, texture: TextureAtlasSprite): IBakedModel {
//        return NormalizedCubeProjectedBakedModel(model, texture)
//    }

    /**
     * Renders a randomized block model for the provided state, world and position.
     */
    fun renderBlockModel(state: IBlockState, world: IBlockAccess, pos: BlockPos) {
        renderBakedModel(getBakedBlockModel(state, world, pos))
    }

    /**
     * Renders a model that was forced to load using this helper's system.
     */
    fun renderBlockModel(loc: ModelResourceLocation, world: IBlockAccess, pos: BlockPos) {
        renderBakedModel(getBakedBlockModel(loc, world, pos))
    }

    fun getLocationFromState(state: IBlockState): ModelResourceLocation? = stateToModelLocationMap[state]

    fun getLocationFromStack(stack: ItemStack): ModelResourceLocation {
        if (!stack.isEmpty) {
            val hashMap = itemModelLocations[stack.item.delegate]

            if (hashMap != null) {
                var loc: ModelResourceLocation? = hashMap.get(stack.metadata)

                if (loc == null) {
                    val def = itemModelDefinitions[stack.item.delegate]

                    if (def != null) loc = def.getModelLocation(stack)
                }

                if (loc != null) return loc
            }
        }

        return missingModelLocation
    }

    fun getStringIDInSetForStack(stack: ItemStack, set: Set<String>, vararg fallbacks: String): String? {
        if (stack.isEmpty) return null

        val resourceLocation = Item.REGISTRY.getNameForObject(stack.item)

        if (resourceLocation != null) {
            val variantName = resourceLocation.resourceDomain + "__" + resourceLocation.resourcePath
            val damageName = variantName + "__" + stack.itemDamage
            if (set.contains(damageName)) {
                return damageName
            } else if (set.contains(variantName)) {
                return variantName
            }
        }

        return fallbacks.firstOrNull { set.contains(it) }
    }

    fun getModelBlock(model: IModel): ModelBlock? {
        if (vanillaModelWrapper.isInstance(model)) {
            if (vanillaModelWrapperModelBlock == null) {
                vanillaModelWrapperModelBlock = ReflectionHelper.findField(vanillaModelWrapper, "model")
            }

            try {
                return vanillaModelWrapperModelBlock!!.get(model) as ModelBlock
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        return null
    }

    fun getRawItemLocation(location: ModelResourceLocation) = ResourceLocation(location.resourceDomain, "item/" + location.resourcePath)

    fun getModel(loc: ResourceLocation): IModel {
        var model: IModel

        try {
            model = ModelLoaderRegistry.getModel(if (loc is ModelResourceLocation) getRawItemLocation(loc) else loc)

            if (model == null) model = ModelLoaderRegistry.getModel(loc)
        } catch (e: Exception) {
            model = ModelLoaderRegistry.getMissingModel()
        }

        return model
    }

    fun getModelBlock(loc: ResourceLocation): ModelBlock? = getModelBlock(getModel(loc))

    fun getModelBlockDefinitionMap(definition: ModelBlockDefinition): Map<String, VariantList> {
        if (modelBlockDefinitionMap == null) {
            modelBlockDefinitionMap = ReflectionHelper.findField(ModelBlockDefinition::class.java, "mapVariants", "field_178332_b")
        }

        try {
            return modelBlockDefinitionMap!!.get(definition) as Map<String, VariantList>
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    fun getBlockstatesVariants(loc: ResourceLocation): Map<String, VariantList> {
        val blockstatesLocation = ResourceLocation(loc.resourceDomain, "blockstates/" + loc.resourcePath + ".json")
        val resources: List<IResource>

        try {
            resources = Minecraft.getMinecraft().resourceManager.getAllResources(blockstatesLocation)
        } catch (e: IOException) {
            GenesisMod.logger.warn("Encountered an IO exception while getting the IResources for location " + blockstatesLocation, e)
            return HashMap()
        }

        val output = HashMap<String, VariantList>()

        try {
            resources
                    .map { InputStreamReader(it.inputStream, StandardCharsets.UTF_8) }
                    .map { ModelBlockDefinition.parseFromReader(it) }
                    .forEach { output.putAll(getModelBlockDefinitionMap(it)) }
        } catch (e: Exception) {
            GenesisMod.logger.warn("Encountered an exception while loading the blockstates json at " + blockstatesLocation, e)
        }

        return output
    }

    fun getDestroyBlockIcon(progress: Int): TextureAtlasSprite {
        return Minecraft
                .getMinecraft()
                .textureMapBlocks
                .getAtlasSprite("minecraft:blocks/destroy_stage_" + progress)
    }

    fun getDestroyBlockIcon(progress: Float): TextureAtlasSprite {
        val progress = MathHelper.clamp(progress, 0f, 1f)
        val index = Math.ceil((progress * 10).toDouble()).toInt() - 1
        return getDestroyBlockIcon(index)
    }

    fun forceModelLoading(state: BlockStateContainer, loc: ResourceLocation) {
        FORCED_MODELS.add(Pair.of(state, loc))
    }

    fun forceModelLoading(name: String, states: Collection<String>, loc: ResourceLocation) {
        if (states.isEmpty()) {
            GenesisMod.logger.warn(IllegalArgumentException("No states provided to force loading for model location '$loc'."))
            return
        }

        val property = object : IProperty<String> {
            override fun getName(): String = name

            override fun getAllowedValues(): Collection<String> = states

            override fun getValueClass(): Class<String> = String::class.java

            override fun getName(value: String): String = value

            override fun parseValue(value: String): Optional<String> {
                return if (allowedValues.contains(value)) Optional.of(value) else Optional.absent()
            }
        }

        try {
            forceModelLoading(BlockStateContainer(FAKE_BLOCK, property), loc)
        } catch (e: RuntimeException) {
            GenesisMod.logger.warn("An error occurred constructing a fake BlockStateContainer object to force loading of '$loc'.")
        }

    }

    fun forceModelLoading(variants: Collection<String>, loc: ResourceLocation) {
        var sharedPropertyName = ""
        val newVariants = ArrayList<String>(variants.size)

        for (variant in variants) {
            val equalsIndex = variant.indexOf("=")

            if (equalsIndex == -1) {
                newVariants.add(variant)
                continue
            }

            val variantPropertyName = variant.substring(0, equalsIndex)

            if (sharedPropertyName == "") {
                sharedPropertyName = variantPropertyName
            } else if (sharedPropertyName != variantPropertyName) {
                throw RuntimeException("Multiple property names found while attempting to create a list of variants in the blockstates json of $loc.")
            }

            newVariants.add(variant.substring(equalsIndex + 1))
        }

        forceModelLoading(sharedPropertyName, newVariants, loc)
    }

    fun forceModelLoading(loc: ResourceLocation) {
        forceModelLoading(getBlockstatesVariants(loc).keys, loc)
    }

    fun forceModelLoading(propertyName: String, loc: ResourceLocation, vararg states: String) {
        forceModelLoading(propertyName, Arrays.asList(*states), loc)
    }

    fun forceModelLoading(actualBlock: Block, loc: ResourceLocation) {
        forceModelLoading(actualBlock.blockState, loc)
    }

    @SubscribeEvent
    fun registerBlock(event: RegistryEvent.Register<Block>) {
        event.registry.register(FAKE_BLOCK.setRegistryName(ResourceLocation(GenesisMod.MOD_ID, "dummy_block")))
    }

    @SubscribeEvent
    fun addForcedModels(event: ModelRegistryEvent) {
        LOCATION_TO_FAKE_STATE.clear()

        for ((key, locationNoVariant) in FORCED_MODELS) {
            for (actualState in key.validStates) {
                val fakeState = FakeBlockState(actualState)
                val location = ModelResourceLocation(locationNoVariant.resourceDomain + ":" + locationNoVariant.resourcePath, getPropertyString(fakeState))

                LOCATION_TO_FAKE_STATE[location] = fakeState
            }
        }

        ModelLoader.setCustomStateMapper(FAKE_BLOCK, { LOCATION_TO_FAKE_STATE.inverse() })
    }
}
