/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neutralplasma.virtusbot.commands

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.neutralplasma.virtusbot.VirtusBot.blackList
import net.dv8tion.jda.api.Permission
import java.util.function.Predicate

abstract class AdminCommand : Command() {
    init {
        this.category = Category("Admin", Predicate { event: CommandEvent ->
            blackList!!.isBlackListed(event.author.id)
                    ||
            event.author.id == event.client.ownerId
                    ||
            event.member.hasPermission(Permission.MANAGE_SERVER)
        })
        guildOnly = true
    }
}