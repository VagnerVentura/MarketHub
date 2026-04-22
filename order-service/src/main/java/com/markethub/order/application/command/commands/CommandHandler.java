package com.markethub.order.application.command.commands;

public interface CommandHandler <C,R>{
    R handle(C command);
}
