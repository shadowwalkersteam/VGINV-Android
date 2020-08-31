package com.techno.vginv.data.fixtures;


import android.net.Uri;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.techno.vginv.Core.ConstantStrings;
import com.techno.vginv.Model.GroupChats;
import com.techno.vginv.Model.MessageData;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.data.model.User;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.features.demo.def.DefaultMessagesActivity;
import com.techno.vginv.utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * Created by troy379 on 12.12.16.
 */
public final class MessagesFixtures extends FixturesData {

    private static String messageSenderID;

    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message sendImageMessage(Uri uri) {
        Message message = new Message(getRandomId(), getSender(), null);
        message.setImage(new Message.Image(uri.toString()));
        return message;
    }

    public static Message sendFileMessage(Uri uri) {
        Message message = new Message(getRandomId(), getSender(), null);
        message.setText(uri.toString());
        return message;
    }

    public static Message sendVoiceMessage(Uri uri, String duration) {
        Message message = new Message(getRandomId(), getSender(), null);
        message.setVoice(new Message.Voice(uri.toString(), duration));
        return message;
    }

    public static Message getImageMessage() {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setImage(new Message.Image(getRandomImage()));
        return message;
    }

    public static Message getImageMessage(MessageData messageData) {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setImage(new Message.Image(messageData.getMessage()));
        return message;
    }

    public static Message getGroupImageMessage(GroupChats messageData) {
        Message message = new Message(getRandomId(), getGroupUser(messageData), null);
        message.setImage(new Message.Image(messageData.getMessage()));
        return message;
    }

    public static Message getVoiceMessage() {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setVoice(new Message.Voice("http://example.com", ""));
        return message;
    }

    public static Message getVoiceMessage(MessageData messageData) {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setVoice(new Message.Voice(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + messageData.getMessage(), ""));
        return message;
    }

    public static Message getVoiceMessage(GroupChats messageData) {
        Message message = new Message(getRandomId(), getGroupUser(messageData), null);
        message.setVoice(new Message.Voice(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + messageData.getMessage(), ""));
        return message;
    }

    public static Message getGroupFileMessage(GroupChats messageData) {
        Message message = new Message(getRandomId(), getUser(), null);
        message.setFile(new Message.File("Attachment",ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + messageData.getMessage(), messageData.getMessage()));
        return message;
    }

    public static Message getTextMessage() {
        int id = SharedInstance.getUserMessages().getMessages().size() - 1;
        MessageData messageData = SharedInstance.getUserMessages().getMessages().get(id);
        return getTextMessage(messageData);
//        return getTextMessage(getRandomMessage());
    }

    public static Message getTextMessage(MessageData messageData) {
        return getTextMessage(messageData.getMessage(), messageData);
    }

    public static Message getFileMessage(MessageData messageData) {
        return getTextMessage(ConstantStrings.URLS.Cloud.WEBSITE_BASE_URL + messageData.getMessage(), messageData);
    }

    public static Message getGroupTextMessage(GroupChats messageData) {
        return getGroupTextMessage(messageData.getMessage(), messageData);
    }

    //not using this method
    public static Message getTextMessage(String text) {
        return new Message(getRandomId(), getUser(), text);
    }

    public static Message getSenderTextMessage(String text) {
        return new Message(getRandomId(), getSender(), text);
    }

    public static Message getTextMessage(String text, MessageData messageData) {
        return new Message(messageData.getId(), getUser(), text);
    }

    public static Message getTextMessage(String text, GroupChats messageData) {
        return new Message(messageData.getId(), getUser(), text);
    }

    public static Message getGroupTextMessage(String text, GroupChats messageData) {
        return new Message(messageData.getId(), getGroupUser(messageData), text, getConvertedDate(messageData.getCreatedAt()));
    }

    public static ArrayList<Message> getMessages() {
        ArrayList<Message> messages = new ArrayList<>();

        try {
            if (DefaultDialogsActivity.isGroupChat) {
                int counter = 1;
                for (GroupChats messageData : SharedInstance.getGroups().getGroupsData()) {
                    if (messageData.getSenderType().equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, "")))) {
                        messageSenderID = messageData.getSenderDetails().getId();
                        Message message = null;
                        if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".jpg") || messageData.getMessage().endsWith(".jpeg") || messageData.getMessage().endsWith(".png"))) {
                            message = getGroupImageMessage(messageData);
                        } else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp3") || messageData.getMessage().endsWith(".wav"))) {
                            message = getVoiceMessage(messageData);
                        } else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp4") ||  messageData.getMessage().endsWith(".mkv") ||  messageData.getMessage().endsWith(".flv"))) {
                            message = getGroupTextMessage(messageData);
                            String res = message.getText();
                            res = "Video " + counter + ": " + res;
                            message.setText(res);
                            counter++;
                        } else {
                            message = getGroupTextMessage(messageData);
                        }
//                        Calendar calendar = Calendar.getInstance();
//                        message.setCreatedAt(calendar.getTime());
                        messages.add(message);
                    }
                }
            } else {
                try {
                    int counter = 1;
                    for (MessageData messageData : SharedInstance.getUserMessages().getMessages()) {
                        messageSenderID = messageData.getSenderID();
                        Message message = null;
                        if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".jpg") || messageData.getMessage().endsWith(".jpeg") || messageData.getMessage().endsWith(".png"))) {
                            message = getImageMessage(messageData);
                        } else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp3") || messageData.getMessage().endsWith(".wav"))) {
                            message = getVoiceMessage(messageData);
                        }   else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp4") ||  messageData.getMessage().endsWith(".mkv") ||  messageData.getMessage().endsWith(".flv"))) {
                            message = getTextMessage(messageData);
                            String res = message.getText();
                            res = "Video " + counter + ": " + res;
                            message.setText(res);
                            counter++;
                        }
                        else {
                            message = getTextMessage(messageData);
                        }
                        Calendar calendar = Calendar.getInstance();
                        message.setCreatedAt(calendar.getTime());
                        messages.add(message);
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 10/*days count*/; i++) {
//            int countPerDay = rnd.nextInt(5) + 1;
//
//            for (int j = 0; j < countPerDay; j++) {
//                Message message;
//                if (i % 2 == 0 && j % 3 == 0) {
//                    message = getImageMessage();
//                } else {
//                    message = getTextMessage();
//                }
//
//                Calendar calendar = Calendar.getInstance();
//                if (startDate != null) calendar.setTime(startDate);
//                calendar.add(Calendar.DAY_OF_MONTH, -(i * i + 1));
//
//                message.setCreatedAt(calendar.getTime());
//                messages.add(message);
//            }
//        }
        return messages;
    }

    public static ArrayList<Message> getRoomMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            int counter = 0;
            for (GroupChats messageData : SharedInstance.getGroups().getGroupsData()) {
                messageSenderID = messageData.getSenderDetails().getId();
                Message message = null;
                if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".jpg") || messageData.getMessage().endsWith(".jpeg") || messageData.getMessage().endsWith(".png"))) {
                    message = getGroupImageMessage(messageData);
                } else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp3") || messageData.getMessage().endsWith(".wav"))) {
                    message = getVoiceMessage(messageData);
                } else if (messageData.getMessage().contains("/chat/attachments") && (messageData.getMessage().endsWith(".mp4") ||  messageData.getMessage().endsWith(".mkv") ||  messageData.getMessage().endsWith(".flv"))) {
                    message = getGroupTextMessage(messageData);
                    String res = message.getText();
                    res = "Video " + counter + ": " + res;
                    message.setText(res);
                    counter++;
                } else {
                    message = getGroupTextMessage(messageData);
                }
                Calendar calendar = Calendar.getInstance();
                message.setCreatedAt(calendar.getTime());
                messages.add(message);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    private static User getUser() {
        Dialog dialog = DefaultMessagesActivity.dialogModel;

//        boolean even = rnd.nextBoolean();
//        return new User(
//                even ? "0" : "1",
//                even ? names.get(0) : names.get(1),
//                even ? avatars.get(0) : avatars.get(1),
//                true);

        return new User(
                messageSenderID,
                dialog.getUsers().get(0).getName(),
                dialog.getDialogPhoto(),
                false);
    }

    private static User getGroupUser(GroupChats messageData) {
        Dialog dialog = DefaultMessagesActivity.dialogModel;

//        boolean even = rnd.nextBoolean();
//        return new User(
//                even ? "0" : "1",
//                even ? names.get(0) : names.get(1),
//                even ? avatars.get(0) : avatars.get(1),
//                true);

        return new User(
                messageData.getSenderDetails().getId(),
                messageData.getSenderDetails().getFirstName(),
                messageData.getSenderDetails().getProfilePicture(),
                false);
    }

    private static User getSender() {
        return new User(
                SharedPrefManager.read(SharedPrefManager.USER_ID, "").toString(),
                SharedPrefManager.read(SharedPrefManager.USER_NAME, ""),
                SharedPrefManager.read(SharedPrefManager.USER_PORIFLE_PIC, ""),
                false);
    }

    public static Date getConvertedDate(String dtStart) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date = DefaultMessagesActivity.df.parse(dtStart);
            if (date != null) {
                return date;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
