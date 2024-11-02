package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserPreferenceController;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Slf4j
public class BudgetCommand implements SlashCommandHandler {

    static final String NAME = "budget";

    @Inject UserPreferenceController userPreferenceController;

    @Inject
    public BudgetCommand() {
        // Empty and public for Dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return NAME;
    }

    static final String BUDGET_PARAMETER_NAME = "maximumbudget";

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Set your maximum monthly budget")
                .addOptions(
                        new OptionData(
                                        OptionType.INTEGER,
                                        BUDGET_PARAMETER_NAME,
                                        "The bot will only suggest rooms at or below this monthly budget")
                                .setRequired(true));
    }

    private String formatBudget(int budget) {
        return "$" + budget + ".00 per month";
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /budget");
        Integer maximumBudget =
                Objects.requireNonNull(event.getOption(BUDGET_PARAMETER_NAME)).getAsInt();

        if (maximumBudget < 0) {
            event.reply("Your budget cannot be negative; please try again.").queue();
            return;
        }

        String discordUserId = event.getUser().getId();

        Integer oldMaximumBudget = userPreferenceController.getMaximumBudgetForUser(discordUserId);

        userPreferenceController.setMaximumBudgetForUser(discordUserId, maximumBudget);

        if (oldMaximumBudget == null) {
            event.reply("Your maximum budget has been set to " + formatBudget(maximumBudget))
                    .queue();
        } else {
            event.reply(
                            "Your maximum budget has been changed from "
                                    + formatBudget(oldMaximumBudget)
                                    + " to "
                                    + formatBudget(maximumBudget))
                    .queue();
        }
    }
}
