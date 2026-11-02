package com.studygroup.service;

import com.studygroup.model.AppUser;
import com.studygroup.model.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long groupId, AppUser sender, String content);

    List<Message> groupMessages(Long groupId);

    List<Message> recentForUser(Long userId);
}
