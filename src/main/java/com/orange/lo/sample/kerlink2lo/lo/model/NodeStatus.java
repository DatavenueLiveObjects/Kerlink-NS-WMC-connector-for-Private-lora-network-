/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class NodeStatus {

    private String status = null;
    
    private Capabilities capabilities = null;
    
    private String lastContact = null;
    
    private Integer sessionSequenceId = null;
    
    private Integer eventSequenceId = null;
    
    
    public static class Capabilities {
        private Command command;

        public Capabilities() {
        }
        
        public Capabilities(boolean commandAvailable) {
            this.command = new Command(commandAvailable);
        }
        
        public Command getCommand() {
            return command;
        }

        public void setCommand(Command command) {
            this.command = command;
        }
        
    }
    
    public static class Command {
        private boolean available;

        public Command() {
         
        }
        
        public Command(boolean available) {
            this.available = available;
        }
        
        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public String getLastContact() {
        return lastContact;
    }

    public void setLastContact(String lastContact) {
        this.lastContact = lastContact;
    }

    public Integer getSessionSequenceId() {
        return sessionSequenceId;
    }

    public void setSessionSequenceId(Integer sessionSequenceId) {
        this.sessionSequenceId = sessionSequenceId;
    }

    public Integer getEventSequenceId() {
        return eventSequenceId;
    }

    public void setEventSequenceId(Integer eventSequenceId) {
        this.eventSequenceId = eventSequenceId;
    }
}