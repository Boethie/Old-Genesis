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
package genesis.util;

public class WorldFlags {
    /**
     * Flag 1
     * <p>
     * Causes a block update.
     */
    public static final int UPDATE_BLOCK = 0b00001;

    /**
     * Flag 2
     * <p>
     * Sends the change to clients.
     */
    public static final int UPDATE_CLIENT = 0b00010;

    /**
     * Flag 3
     * <p>
     * Causes a block update and sends the change to clients.
     */
    public static final int UPDATE_BLOCK_AND_CLIENT = UPDATE_BLOCK | UPDATE_CLIENT;

    /**
     * Flag 4
     * <p>
     * Prevents the block from being re-rendered.
     */
    public static final int PREVENT_RERENDER = 0b00100;

    /**
     * Flag 8
     * <p>
     * If [FLAG_PREVENT_RERENDER] is clear, forces any re-renders to run on the main
     * thread instead of the worker pool.
     */
    public static final int RERENDER_ON_MAIN = 0b01000;

    /**
     * Flag 10
     * <p>
     * Sends the change to clients and forces any re-renders to run on the main
     * thread instead of the worker pool.
     */
    public static final int UPDATE_CLIENT_AND_RERENDER_ON_MAIN = UPDATE_CLIENT | RERENDER_ON_MAIN;

    /**
     * Flag 11
     * <p>
     * Causes a block update, sends the change to clients, and forces any
     * re-renders to run on the main thread instead of the worker pool.
     */
    public static final int UPDATE_BLOCK_AND_CLIENT_AND_RERENDER_ON_MAIN = UPDATE_BLOCK | UPDATE_CLIENT | RERENDER_ON_MAIN;

    /**
     * Flag 16
     * <p>
     * Prevents observers from seeing this change.
     */
    public static final int PREVENT_OBSERVER_UPDATE = 0b10000;

    /**
     * Flag 18
     * <p>
     * Sends the change to clients and prevents observers from seeing this change.
     */
    public static final int UPDATE_CLIENT_AND_PREVENT_OBSERVER_UPDATE = UPDATE_CLIENT | PREVENT_OBSERVER_UPDATE;

    /**
     * Flag 20
     * <p>
     * Prevents the block from being re-rendered and observers from seeing this change.
     */
    public static final int PREVENT_RERENDER_AND_OBSERVER_UPDATE = PREVENT_RERENDER | PREVENT_OBSERVER_UPDATE;
}
