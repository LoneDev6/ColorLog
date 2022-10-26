package dev.lone.utils;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the normal Bukkit Logger to write Colors
 * Based on Timeout's implementation: https://www.spigotmc.org/threads/87576/
 *
 * @author LoneDev
 */
public class ColorLog
{
    private Logger logger;
    private static final Pattern COLOR_SEARCH_PATTERN = Pattern.compile(String.format("((%c|%c)[0-9a-fk-or])(?!.*\1)", 0x00A7, 0x0026));
    private static final String COLOR_PATTERN = "\u001b[38;5;%dm";
    private static final String FORMAT_PATTERN = "\u001b[%dm";

    private String prefix;

    /**
     * Creates a logger with no prefix.
     */
    public ColorLog()
    {
        this("");
    }

    /**
     * Creates a new ColorLog with prefix.
     *
     * @param prefix the prefix of the plugin
     */
    public ColorLog(String prefix)
    {
        this.prefix = applyColors(prefix);
        logger = Bukkit.getLogger();
    }

    /**
     * Creates a new ColorLog with prefix and a specific logger hooked.
     *
     * @param prefix the prefix of the plugin
     * @param logger the logger
     */
    public ColorLog(String prefix, Logger logger)
    {
        this.prefix = applyColors(prefix);
        this.logger = logger;
    }

    /**
     * Sets a new prefix for this ColorLog.
     *
     * @param prefix the new prefix of the plugin
     */
    public void setPrefix(String prefix)
    {
        this.prefix = applyColors(prefix);
    }

    /**
     * Changes the current logger.
     *
     * @param logger the logger
     */
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * Logs a message in the console. See {@link Logger#log(Level, String)}.
     *
     * @param level   The level of the log
     * @param message the message you want to show
     */
    public void log(Level level, String message)
    {
        logger.log(level, prefix + applyColors(message));
    }

    /**
     * Logs a message in the console. See {@link Logger#log(Level, String, Throwable)}.
     *
     * @param level   the Level of the log
     * @param message the message
     * @param e       the exception
     */
    public void log(Level level, String message, Throwable e)
    {
        logger.log(level, prefix + applyColors(message), e);
    }

    /**
     * Converts a String with Minecraft-ColorCodes into Ansi-Colors.
     * Returns null if the string is null.
     *
     * @param string the string.
     * @return the converted string or null if the string is null
     */
    private static String applyColors(String string)
    {
        if (!string.isEmpty())
        {
            Matcher matcher = COLOR_SEARCH_PATTERN.matcher(string);

            if (matcher.groupCount() > 0)
            {
                String coloredMessage = String.copyValueOf(string.toCharArray()) + ConsoleColor.RESET.ansiColor;
                while (matcher.find())
                {
                    String result = matcher.group(1);
                    ConsoleColor color = ConsoleColor.getColorByCode(result.charAt(1));
                    if (color == null)
                        continue;
                    coloredMessage = coloredMessage.replace(result, color.getAnsiColor());
                }
                return coloredMessage;
            }
        }
        return string;
    }

    /**
     * Represents a set of Minecraft-ColorCodes and their ANSI-Codes.
     */
    public enum ConsoleColor
    {
        BLACK('0', COLOR_PATTERN, 0),
        DARK_GREEN('2', COLOR_PATTERN, 2),
        DARK_RED('4', COLOR_PATTERN, 1),
        GOLD('6', COLOR_PATTERN, 172),
        DARK_GREY('8', COLOR_PATTERN, 8),
        GREEN('a', COLOR_PATTERN, 10),
        RED('c', COLOR_PATTERN, 9),
        YELLOW('e', COLOR_PATTERN, 11),
        DARK_BLUE('1', COLOR_PATTERN, 4),
        DARK_AQUA('3', COLOR_PATTERN, 30),
        DARK_PURPLE('5', COLOR_PATTERN, 54),
        GRAY('7', COLOR_PATTERN, 246),
        BLUE('9', COLOR_PATTERN, 4),
        AQUA('b', COLOR_PATTERN, 51),
        LIGHT_PURPLE('d', COLOR_PATTERN, 13),
        WHITE('f', COLOR_PATTERN, 15),
        STRIKETHROUGH('m', FORMAT_PATTERN, 9),
        ITALIC('o', FORMAT_PATTERN, 3),
        BOLD('l', FORMAT_PATTERN, 1),
        UNDERLINE('n', FORMAT_PATTERN, 4),
        RESET('r', FORMAT_PATTERN, 0);

        private final char bukkitColor;
        private final String ansiColor;

        ConsoleColor(char bukkitColor, String pattern, int ansiCode)
        {
            this.bukkitColor = bukkitColor;
            this.ansiColor = String.format(pattern, ansiCode);
        }

        /**
         * Searches if the code is a valid colorcode and returns the right enum
         *
         * @param code the Minecraft-ColorCode without Formatter-Char
         * @return the Color enum or null if no enum can be found
         */
        @Nullable
        public static ConsoleColor getColorByCode(char code)
        {
            for (ConsoleColor color : values())
            {
                if (color.bukkitColor == code)
                    return color;
            }
            return null;
        }

        /**
         * Returns the ANSI-ColorCode of the Colorcode
         *
         * @return the Ansi-ColorCode
         */
        public String getAnsiColor()
        {
            return ansiColor;
        }
    }
}
