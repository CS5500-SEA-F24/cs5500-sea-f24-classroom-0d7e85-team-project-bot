package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UserPreference implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    // e.g. event.getUser().getId()
    String discordUserId;

    // The user wants to be referred to by this name
    String preferredName;

    // The maximum amount (in USD) that a user is willing to pay per month in rent
    // null means the user has not yet set a budget
    @Nullable @Nonnegative Integer maximumBudget;
}
