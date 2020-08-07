package com.neutralplasma.virtusbot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AbstractChatUtil extends ListenerAdapter {
    private static JDA jda ;

    private static final List<String> registered = new ArrayList<>();

    private User user;
    private final ChatConfirmHandler handler;

    private OnClose onClose = null;
    private ListenerAdapter listener;


    public AbstractChatUtil(User user, ChatConfirmHandler confirmHandler, JDA jda){
        this.user = user;
        this.handler = confirmHandler;

        initializeListeners(jda);
        registered.add(user.getId());
    }

    public static boolean isRegistered(User player) {
        return registered.contains(player.getId());
    }

    public static boolean unregister(User player) {
        return registered.remove(player.getId());
    }


    public void initializeListeners(JDA jda){
        this.listener = new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
                User user = event.getAuthor();
                if (!AbstractChatUtil.isRegistered(user)) return;
                AbstractChatUtil.unregister(user);

                ChatConfirmEvent chatConfirmEvent = new ChatConfirmEvent(user, event.getMessage().getContentRaw());

                handler.onChat(chatConfirmEvent);

                if(onClose != null){
                    onClose.onClose();
                }
                jda.removeEventListener(listener);
            }
        };

        jda.addEventListener(listener);
    }



    public interface OnClose {
        void onClose();
    }

    public void setOnClose(OnClose onClose) {
        this.onClose = onClose;
    }

    public interface ChatConfirmHandler {
        void onChat(ChatConfirmEvent event);
    }

    public class ChatConfirmEvent {

        private final User user;
        private final String message;

        public ChatConfirmEvent(User user, String message) {
            this.user = user;
            this.message = message;
        }

        public User getUser() {
            return user;
        }

        public String getMessage() {
            return message;
        }
    }
}
