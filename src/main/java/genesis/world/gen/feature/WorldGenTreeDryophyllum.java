package genesis.world.gen.feature;

import static net.minecraft.block.BlockLeaves.CHECK_DECAY;
import static net.minecraft.block.BlockLog.LOG_AXIS;

import genesis.init.GenesisBlocks;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

public class WorldGenTreeDryophyllum extends WorldGenAbstractGenesisTree {

    private static final IBlockState LOG = GenesisBlocks.DRYOPHYLLUM_LOG.getDefaultState();
    private static final IBlockState LEAF = GenesisBlocks.DRYOPHYLLUM_LEAVES.getDefaultState().withProperty(CHECK_DECAY, false);
    private final DryophyllumVariant variant;

    public WorldGenTreeDryophyllum(DryophyllumVariant variant, int minHeight, int maxHeight) {
        super(minHeight, maxHeight, true);
        this.variant = variant;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = this.getTreeHeight(rand);
        int trunkHeight = height - this.variant.leavesHeightTotal;

//        if (!BlockVolumeShape.region(-1, 1, -1, 1, trunkHeight - 1, 1)
//                .and(-3, trunkHeight, -3, 3, height, 3)
//                .hasSpace(pos, isEmptySpace(world)))
//            return false;

        for (int i = 0; i < trunkHeight; i++) {
            setBlockInWorld(world, pos.up(i), LOG.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
        }

        generateTreeLeavesAndBranches(world, pos.add(0, trunkHeight, 0), pos, rand);

        return true;
    }

    private void generateTreeLeavesAndBranches(World world, BlockPos trunkTop, BlockPos treeBottom, Random rand) {

        for (int i = 0; i < this.variant.branchTargetsAmount; i++) {
            double r = this.variant.xzBranchTargetSpread;
            double yr = this.variant.yBranchTargetSpread * 2;

            Vec3d currentPos = new Vec3d(trunkTop).add(new Vec3d(
                    (rand.nextDouble() * 2 - 1) * r, (rand.nextDouble()) * yr, (rand.nextDouble() * 2 - 1) * r
            ));
            BlockPos branchSrc = trunkTop.up(MathHelper.getInt(rand, this.variant.branchSourceRelativeMinY, this.variant.branchSourceRelativeMaxY));
            double dy = currentPos.y - trunkTop.getY();
            double maxLen = Math.max(yr, trunkTop.getY() - treeBottom.getY());
            if (Math.abs(dy) > maxLen) {
                branchSrc = branchSrc.up((int) Math.round(Math.abs(dy) - maxLen));
                if (branchSrc.getY() > trunkTop.getY()) {
                    int diff = branchSrc.getY() - trunkTop.getY();
                    branchSrc = branchSrc.down(diff);
                    currentPos = currentPos.addVector(0, diff, 0);
                }
            }
            if (Math.round(currentPos.y) < treeBottom.getY() + MathHelper.ceil(this.variant.leavesRadius) + 2) {
                currentPos = new Vec3d(currentPos.x, treeBottom.getY() + MathHelper.ceil(this.variant.leavesRadius) + 2, currentPos.z);
            }

            generateBranch(world, rand, branchSrc, currentPos);
        }

    }

    private static boolean xzEqual(Vec3i vecA, Vec3i vecB) {
        return vecA.getX() == vecB.getX() && vecA.getZ() == vecB.getZ();
    }

    private void generateBranch(World world, Random rand, BlockPos trunkPos, Vec3d endPos) {
        Vec3d curr = new Vec3d(trunkPos);
        Vec3d next = next(world, curr, endPos.subtract(curr).normalize(), endPos, trunkPos);
        BlockPos prev;
        do {
            BlockPos currBlock = new BlockPos(curr);
            Vec3d dir = endPos.subtract(curr).normalize();
            prev = currBlock;
            curr = next;
            next = next(world, curr, dir, endPos, trunkPos);

            IBlockState state = (xzEqual(currBlock, trunkPos) ? LOG : LOG.withProperty(BlockLog.LOG_AXIS, getLogAxis(world, currBlock, dir)));
            this.setBlockInWorld(world, currBlock, state);

            // check to avoid long straight up branches
            BlockPos nextBlock = new BlockPos(next);
            if (endPos.squareDistanceTo(next) > Math.sqrt(3) && xzEqual(prev, currBlock) && xzEqual(currBlock, nextBlock)) {
                next = next.addVector(rand.nextBoolean() ? -1 : 1, 0, rand.nextBoolean() ? -1 : 1);
            }
        } while (endPos.squareDistanceTo(curr) > Math.sqrt(3));

        this.generateLeaves(world, rand, curr);
        this.generateLeaves(world, rand, new Vec3d(prev));
    }

    private void generateLeaves(World world, Random rand, Vec3d pos) {
        int leavesRadius = MathHelper.ceil(this.variant.leavesRadius);
        double expConst = this.variant.distanceExpConstant;

        for (int dx = -leavesRadius; dx <= leavesRadius; dx++) {
            for (int dy = -leavesRadius; dy <= leavesRadius; dy++) {
                for (int dz = -leavesRadius; dz <= leavesRadius; dz++) {
                    // lower value of p makes it closer to diamond shape, higher value makes it closer to cube, value p=2 is perfect sphere.
                    if (Math.pow(Math.abs(dx), expConst) + Math.pow(Math.abs(dy), expConst) + Math.pow(Math.abs(dz), expConst)
                            - rand.nextFloat() * this.variant.leavesShapeRandomization * Math.pow(leavesRadius, expConst) <= Math
                            .pow(leavesRadius, expConst)) {
                        setBlockInWorld(world, new BlockPos(pos.addVector(dx, dy, dz)), LEAF);
                    }
                }
            }
        }
    }


    private Vec3d next(World world, Vec3d previousPos, Vec3d direction, Vec3d target, Vec3i trunkSrcPos) {
        Vec3d bestPos = null;
        double bestDist = previousPos.squareDistanceTo(target) - 0.5;

        Vec3d bestNoTrunkPos = null;
        double bestNoTrunkDist = previousPos.squareDistanceTo(target) - 0.5;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                // try to find point closer to target that isn't at trunk XZ coords.
                BlockPos blockPosXZ = new BlockPos(previousPos.addVector(dx, 0, dz));
                if (xzEqual(trunkSrcPos, blockPosXZ)) {
                    Vec3d newPos = new Vec3d(blockPosXZ);
                    double dist = newPos.squareDistanceTo(target);
                    if (dist < bestNoTrunkDist) {
                        bestNoTrunkPos = newPos;
                        bestNoTrunkDist = dist;
                    }
                }
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        // find surrounding existing blocks that aren't trunk
                        BlockPos blockPos = new BlockPos(previousPos.addVector(dx, dy, dz));
                        if (!xzEqual(trunkSrcPos, blockPos) && world.getBlockState(blockPos).getBlock() == LOG.getBlock()) {
                            Vec3d newPos = new Vec3d(blockPos);
                            double dist = newPos.squareDistanceTo(target);
                            if (dist < bestDist) {
                                bestPos = newPos;
                                bestDist = dist;
                            }
                        }
                    }
                }
            }
        }
        Vec3d nextDirect = previousPos;
        BlockPos origPos = new BlockPos(previousPos);
        do {
            nextDirect = nextDirect.add(direction);
        }
        while (origPos.equals(new BlockPos(nextDirect)));

        if (bestPos != null) {
            Vec3d trunkVec = new Vec3d(trunkSrcPos);
            double diff = 1 + previousPos.subtract(trunkVec).normalize().dotProduct(target.subtract(previousPos).normalize());
            double distDirect = nextDirect.squareDistanceTo(target);
            double distBest = bestPos.squareDistanceTo(target);
            if (distBest < distDirect + diff) {
                return bestPos;
            }
        }
        if (bestNoTrunkPos != null) {
            return bestNoTrunkPos;
        }
        return nextDirect;
    }

    private BlockLog.EnumAxis getLogAxis(World world, BlockPos pos, Vec3d dir) {
        // this finds the right direction based on neighbors
        int weightX = 0, weightY = 0, weightZ = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx != 0 || dy != 0 || dz != 0) {
                        if (world.getBlockState(pos.add(dx, dy, dz)).getBlock() == LOG.getBlock()) {
                            weightX += dx == 0 ? 0 : 1;
                            weightY += dy == 0 ? 0 : 1;
                            weightZ += dz == 0 ? 0 : 1;
                        }
                    }
                }
            }
        }
        BlockLog.EnumAxis axis = BlockLog.EnumAxis.NONE;
        // when X wins
        if (weightX > weightY && weightX > weightZ) {
            axis = BlockLog.EnumAxis.X;
        }
        // when horizontal is ambiguous and Y is smaller
        else if (weightX == weightZ && weightX > weightY) {
            axis = BlockLog.EnumAxis.NONE;
        }
        // some of this is probably redundant, but check if Y wins
        else if (weightY > weightZ && weightY > weightX) {
            axis = BlockLog.EnumAxis.Y;
        } else if (weightZ > weightY && weightZ > weightX) {
            axis = BlockLog.EnumAxis.Z;
        }

        if (axis == BlockLog.EnumAxis.NONE) {
            double dx = dir.x * dir.x;
            double dy = dir.y * dir.y;
            double dz = dir.z * dir.z;
            if (dx > dy + dz) {
                axis = BlockLog.EnumAxis.X;
            } else if (dy > dx + dz) {
                axis = BlockLog.EnumAxis.Y;
            } else if (dz > dx + dy) {
                axis = BlockLog.EnumAxis.Z;
            }
        }
        return axis;
    }


    public enum DryophyllumVariant {
        TYPE_1(8, -3, 2.0D, 2.2D),
        TYPE_2(15, -4, 4.3D, 3.5D);

        private final int branchTargetsAmount;
        private final int branchSourceRelativeMinY;
        private final int branchSourceRelativeMaxY = -1;
        private final double leavesRadius = 2.2D;
        private final double xzBranchTargetSpread;
        private final double yBranchTargetSpread;
        private final double distanceExpConstant = 1.6D;
        private final double leavesShapeRandomization = 1.0D;
        private final int leavesHeightTotal;

        private DryophyllumVariant(int branchTargetsAmount, int branchSourceRelativeMinY, double xzBranchTargetSpread, double yBranchTargetSpread) {
            this.branchTargetsAmount = branchTargetsAmount;
            this.branchSourceRelativeMinY = branchSourceRelativeMinY;
            this.xzBranchTargetSpread = xzBranchTargetSpread;
            this.yBranchTargetSpread = yBranchTargetSpread;
            this.leavesHeightTotal = (MathHelper.ceil(this.leavesRadius + this.yBranchTargetSpread * 2));
        }

    }

}
