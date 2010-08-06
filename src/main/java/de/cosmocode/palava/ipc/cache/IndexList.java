/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.cache;


import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Tobias Sarnowski
 */
final class IndexList implements Set<CacheKey>, Serializable {

    private Set<CacheKey> cacheKeys;

    IndexList() {
        cacheKeys = Sets.newHashSet();
    }

    public int size() {
        return cacheKeys.size();
    }

    public boolean isEmpty() {
        return cacheKeys.isEmpty();
    }

    public boolean contains(Object o) {
        return cacheKeys.contains(o);
    }

    public Iterator<CacheKey> iterator() {
        return cacheKeys.iterator();
    }

    public Object[] toArray() {
        return cacheKeys.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return cacheKeys.toArray(a);
    }

    public boolean add(CacheKey cacheKey) {
        return cacheKeys.add(cacheKey);
    }

    public boolean remove(Object o) {
        return cacheKeys.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return cacheKeys.containsAll(c);
    }

    public boolean addAll(Collection<? extends CacheKey> c) {
        return cacheKeys.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return cacheKeys.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return cacheKeys.removeAll(c);
    }

    public void clear() {
        cacheKeys.clear();
    }

    @Override
    public boolean equals(Object o) {
        return cacheKeys.equals(o);
    }

    @Override
    public int hashCode() {
        return cacheKeys.hashCode();
    }
}