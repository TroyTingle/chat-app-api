package com.ttingle.chat_app_api.dto.chat;

public class GroupChatRequest {

    private String[] participants;
    private String groupName;

    // Getters and Setters
    public String[] getParticipants() {
        return participants;
    }
    public void setParticipants(String[] participants) {
        this.participants = participants;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
