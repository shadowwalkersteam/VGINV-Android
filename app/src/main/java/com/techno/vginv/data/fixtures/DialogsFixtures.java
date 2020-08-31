package com.techno.vginv.data.fixtures;

import android.content.Intent;

import com.techno.vginv.DashboardActivity;
import com.techno.vginv.Model.GroupChats;
import com.techno.vginv.Model.Groups;
import com.techno.vginv.Model.LastMessageData;
import com.techno.vginv.Model.RoomDetails;
import com.techno.vginv.SharedInstance;
import com.techno.vginv.data.model.Dialog;
import com.techno.vginv.data.model.Message;
import com.techno.vginv.data.model.User;
import com.techno.vginv.features.demo.def.DefaultDialogsActivity;
import com.techno.vginv.utils.CloudDataService;
import com.techno.vginv.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/*
 * Created by Anton Bevza on 07.09.16.
 */
public final class DialogsFixtures extends FixturesData {
    private DialogsFixtures() {
        throw new AssertionError();
    }
    static HashMap<String, String> hashMap = new HashMap<>();

    public static ArrayList<Dialog> getDialogs() {
        ArrayList<Dialog> chats = new ArrayList<>();
        try {
            if (DefaultDialogsActivity.isGroupChat) {
                Calendar calendar = Calendar.getInstance();
                chats.add(getGroupDialog(SharedInstance.getGroups(), calendar.getTime()));
            } else {
                for (LastMessageData model : SharedInstance.getAllChats().getChats()) {
                    Calendar calendar = Calendar.getInstance();
                    chats.add(getDialog(model, calendar.getTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 1; i++) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
//            calendar.add(Calendar.MINUTE, -(i * i));
//
//            chats.add(getDialog(i, calendar.getTime()));
//        }

        return chats;
    }

    public static ArrayList<Dialog> getRoomDialogs() {
        ArrayList<Dialog> chats = new ArrayList<>();
        try {
            for (RoomDetails model : SharedInstance.getRooms().getAllRooms()) {
                Calendar calendar = Calendar.getInstance();
                chats.add(getRoomDialog(model, calendar.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chats;
    }

//    private static Dialog getDialog(int i, Date lastMessageCreatedAt) {
//        ArrayList<User> users = getUsers();
//        return new Dialog(
//                getRandomId(),
//                users.size() > 1 ? groupChatTitles.get(users.size() - 2) : users.get(0).getName(),
//                users.size() > 1 ? groupChatImages.get(users.size() - 2) : getRandomAvatar(),
//                users,
//                getMessage(lastMessageCreatedAt),
//                i < 3 ? 3 - i : 0);
//    }

    private static Dialog getDialog(LastMessageData model, Date lastMessageCreatedAt) {
        ArrayList<User> users = getUsers(model);
        return new Dialog(
                model.getId(),
                users.get(0).getName(),
                users.get(0).getAvatar(),
                users,
                getMessage(lastMessageCreatedAt, model),
                0);
    }

    private static Dialog getRoomDialog(RoomDetails model, Date lastMessageCreatedAt) {
        ArrayList<User> users = getRoomUsers(model);
        return new Dialog(
                model.getId(),
                users.get(0).getName(),
                users.get(0).getAvatar(),
                users,
                getRoomMessage(lastMessageCreatedAt, model),
                0);
    }

    private static Dialog getGroupDialog(Groups model, Date lastMessageCreatedAt) {
        ArrayList<User> users = getGroupUsers();
        String groupName = printMap(hashMap).toString();
        return new Dialog(
                getRandomId(),
                users.size() > 1 ? groupName : users.get(0).getName(),
                users.get(0).getAvatar(),
                users,
                getGroupMessage(lastMessageCreatedAt, model),
                0);
    }

    public static StringBuilder printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            stringBuilder.append(pair.getValue());
            stringBuilder.append(",");
            it.remove();
        }
        return stringBuilder;
    }


    private static ArrayList<User> getUsers(LastMessageData model) {
        ArrayList<User> users = new ArrayList<>();

//        int usersCount = 1 + rnd.nextInt(4);
//
//        for (int i = 0; i < usersCount; i++) {
//            users.add(getUser());
//        }
        users.add(getUser(model));
        return users;
    }

    private static ArrayList<User> getRoomUsers(RoomDetails model) {
        ArrayList<User> users = new ArrayList<>();
        users.add(getRoomUser(model));
        return users;
    }

    private static ArrayList<User> getGroupUsers() {
        ArrayList<User> users = new ArrayList<>();

        for (GroupChats groupChats : SharedInstance.getGroups().getGroupsData()) {
            try {
                if (users.size() > 0) {
                    if (!hashMap.containsKey(groupChats.getSenderDetails().getId()) && groupChats.getSenderType().equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, "")))) {
                        User user = getGroupUser(groupChats);
                        users.add(user);
                        hashMap.put(user.getId(), user.getName());
                    }
                } else {
                    if (groupChats.getSenderType().equalsIgnoreCase(SharedPrefManager.read(SharedPrefManager.TOGGLER_USER_TYPE, SharedPrefManager.read(SharedPrefManager.USER_TYPE, "")))) {
                        User user = getGroupUser(groupChats);
                        users.add(user);
                        hashMap.put(user.getId(), user.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    private static User getUser(LastMessageData model) {
//        return new User(
//                getRandomId(),
//                getRandomName(),
//                getRandomAvatar(),
//                getRandomBoolean());
        if (model.getReceiverID().equals(SharedPrefManager.read(SharedPrefManager.USER_ID, ""))) {
            return new User(
                    model.getSenderID(),
                    model.getSenderDetails().getFirstName() + " " + model.getSenderDetails().getLastName(),
                    model.getSenderDetails().getProfilePicture(),
                    false);
        } else {
            return new User(
                    model.getReceiverID(),
                    model.getReceiverDetails().getFirstName() + " " + model.getReceiverDetails().getLastName(),
                    model.getReceiverDetails().getProfilePicture(),
                    false);
        }
    }

    private static User getRoomUser(RoomDetails model) {
        return new User(
                model.getSenderId(),
                model.getRoomName(),
                model.getRoomImage(),
                false);
    }

    private static User getGroupUser(GroupChats model) {
        return new User(
                model.getSenderDetails().getId(),
                model.getSenderDetails().getFirstName() + " " + model.getSenderDetails().getLastName(),
                model.getSenderDetails().getProfilePicture(),
                false);
    }

    private static Message getMessage(final Date date, LastMessageData model) {
        return new Message(
                getRandomId(),
                getUser(model),
                model.getMessage(),
                date);
    }

    private static Message getRoomMessage(final Date date, RoomDetails model) {
        return new Message(
                getRandomId(),
                getRoomUser(model),
                model.getMessage(),
                date);
    }

    private static Message getGroupMessage(final Date date, Groups model) {
        return new Message(
                getRandomId(),
                getGroupUser(model.getGroupsData().get(model.getGroupsData().size() - 1)),
                model.getGroupsData().get(model.getGroupsData().size() - 1).getMessage(),
                date);
    }
}
