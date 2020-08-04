package com.neutralplasma.virtusbot.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AbstractReactionUtil extends ListenerAdapter {

    private static final List<String> registered = new ArrayList<>();

    private final ChatConfirmHandler handler;

    private OnClose onClose = null;
    private ListenerAdapter listener;

    private final String emoji;
    private String message;


    public AbstractReactionUtil(User user, ChatConfirmHandler confirmHandler, JDA jda, String emoji, String message){
        this.handler = confirmHandler;
        this.emoji = emoji;
        this.message = message;

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
            public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
                User user = event.getUser();
                if(user == null) return;
                if (!AbstractReactionUtil.isRegistered(user)) return;
                if(!event.getMessageId().equals(message)) return;
                MessageReaction.ReactionEmote emote = event.getReactionEmote();
                if(!emote.getEmoji().equalsIgnoreCase(emoji)) return;

                AbstractReactionUtil.unregister(user);

                ChatConfirmEvent chatConfirmEvent = new ChatConfirmEvent(user, emote.getEmoji());

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
        private final String emote;

        public ChatConfirmEvent(User user, String emote) {
            this.user = user;
            this.emote = emote;
        }

        public User getUser() {
            return user;
        }

        public String getMessage() {
            return emote;
        }
    }
}
