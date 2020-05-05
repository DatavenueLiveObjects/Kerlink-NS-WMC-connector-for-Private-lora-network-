package com.orange.lo.sample.kerlink2lo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class CommandMapper {

    private static Map<String, LoCommand> map = new ConcurrentHashMap<String, LoCommand>();
    
    public void put(String kerlinkId, String loId, String nodeId) {
        map.put(kerlinkId, new LoCommand(loId, nodeId));
    }
    
    public Optional<LoCommand> get(String kerlinkId) {
        return Optional.ofNullable(map.remove(kerlinkId));
    }
    
    public class LoCommand {
        private String id;
        private String nodeId;
        
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
