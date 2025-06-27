package com.infrium.game.util;

public class CommandUsage {
    public final Carg[] arguments;

    public CommandUsage(Carg... arguments) {
        this.arguments = arguments;
        for(int i = 0; i < arguments.length - 1; i++) {
            if(arguments[i] == Carg.OPTIONAL || arguments[i] == Carg.MESSAGE) {
                throw new IllegalArgumentException("ArgTypes OPTIONAL or MESSAGE must be last.");
            }
        }
    }

    public boolean validate(String[] parameters) {
        if(arguments.length == 0) {
            return true;
        }
        for(int i = 0; i < arguments.length; i++) {
            switch(arguments[i]) {
                case BYTE:
                    if(!hasLength(parameters, i) || !isByte(parameters[i])) {
                        return false;
                    }
                    break;
                case SHORT:
                    if(!hasLength(parameters, i) || !isShort(parameters[i])) {
                        return false;
                    }
                    break;
                case INT:
                    if(!hasLength(parameters, i) || !isInt(parameters[i])) {
                        return false;
                    }
                    break;
                case FLOAT:
                    if(!hasLength(parameters, i) || !isFloat(parameters[i])) {
                        return false;
                    }
                    break;
                case DOUBLE:
                    if(!hasLength(parameters, i) || !isDouble(parameters[i])) {
                        return false;
                    }
                    break;
                case LONG:
                    if(!hasLength(parameters, i) || !isLong(parameters[i])) {
                        return false;
                    }
                    break;
                case BOOLEAN:
                    if(!hasLength(parameters, i) || isEmpty(parameters[i])) {
                        return false;
                    }
                    break;
                case STRING:
                    if(!hasLength(parameters, i) || isEmpty(parameters[i])) {
                        return false;
                    }
                    break;
                case MESSAGE:
                    if(!hasLength(parameters, i) || isEmpty(parameters[i])) {
                        return false;
                    }
                    return true;
                case OPTIONAL:
                    if(hasLength(parameters, i) && isEmpty(parameters[i])) {
                        return false;
                    }
                    return true;
                default:
                    break;
            }
        }
        return true;
    }

    public boolean hasLength(String[] parameters, int requirement) {
        return parameters.length - 1 >= requirement;
    }

    public boolean isByte(String parameter) {
        try {
            Byte.parseByte(parameter);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isShort(String parameter) {
        try {
            Short.parseShort(parameter);
            return true;
        } catch(Exception e) {}
        return false;
    }


    public boolean isInt(String parameter) {
        try {
            Integer.parseInt(parameter);
            return true;
        } catch(Exception e) {}
        return false;
    }

    public boolean isFloat(String parameter) {
        try {
            Float.parseFloat(parameter);
            return true;
        } catch(Exception e) {}
        return false;
    }

    public boolean isDouble(String parameter) {
        try {
            Double.parseDouble(parameter);
            return true;
        } catch(Exception e) {}
        return false;
    }

    public boolean isLong(String parameter) {
        try {
            Long.parseLong(parameter);
            return true;
        } catch(Exception e) {}
        return false;
    }

    public boolean isEmpty(String parameter) {
        return parameter.length() <= 0;
    }
}
