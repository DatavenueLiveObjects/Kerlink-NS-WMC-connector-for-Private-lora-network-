/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandMapper {

    private static final Map<String, LoCommand> map = new ConcurrentHashMap<>();
    
    public void put(String kerlinkId, String loId, String nodeId) {
        map.put(kerlinkId, new LoCommand(loId, nodeId));
    }
    
    public Optional<LoCommand> get(String kerlinkId) {
        return Optional.ofNullable(map.remove(kerlinkId));
    }
    
    public class LoCommand {
        private final String id;
        private final String nodeId;
        
        public LoCommand(String id, String nodeId) {
            this.id = id;
            this.nodeId = nodeId;
        }

        public String getId() {
            return id;
        }

        public String getNodeId() {
            return nodeId;
        }
    }
}
