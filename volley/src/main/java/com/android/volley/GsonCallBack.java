/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

import com.android.volley.base.NetBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Encapsulates a parsed response for delivery.
 * <p/>
 */
public class GsonCallBack {
    protected NetBean netBean = new NetBean();

    /**
     * Callback interface for delivering parsed responses.
     */
    public static abstract class Listener<T> {
        public Class mType;
        /**
         * Called when a response is received.
         */
        public abstract void onResponse(T response);

        public Listener() {
            getGenericType();
        }

        private void getGenericType() {
            Type type = super.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = ((ParameterizedType) type);
                Type[] args = ptype.getActualTypeArguments();
                mType = args[0].getClass();
            }
        }

    }
}
