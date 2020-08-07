package com.neutralplasma.virtusbot.handlers.locale;

import org.jetbrains.annotations.NotNull;

public enum Locale {
    TICKET_HELP_CREATE_REACT_CONTENT("`React with \uD83D\uDCAC to create the ticket.`"),
    TICKET_HELP_CREATE_REACT_TITLE("Ticket Creation."),
    TICKET_HELP_CREATE_REACT_FIELD_TITLE("React with \uD83D\uDCAC to create the ticket."),
    SUGGEST_CREATE_CONTENT("`Type {prefix}suggest <suggestion> - to create new suggestion.`"),
    SUGGEST_CREATE_FIELD_TITLE("Suggestion"),
    SUGGEST_CREATE_TITLE("Suggestion"),
    VOTE_TITLE("Vote"),
    VOTE_FIELD_TITLE("Vote info:"),
    ERROR_FIELD_TITLE("ERROR");


    Locale(String text){

    }

    public @NotNull String getText(){
        return this.name();
    }
}