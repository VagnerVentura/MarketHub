package com.markethub.order.application.command;

import java.util.UUID;

public interface CommandHandler <C,R>{
    R handle(C command);
}
