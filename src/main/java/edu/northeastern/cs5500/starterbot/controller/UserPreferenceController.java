package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.UserPreference;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.service.FakeOpenTelemetryService;
import edu.northeastern.cs5500.starterbot.service.OpenTelemetry;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import java.util.Collection;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

public class UserPreferenceController {

    GenericRepository<UserPreference> userPreferenceRepository;
    @Inject OpenTelemetry openTelemetry;

    @Inject
    UserPreferenceController(GenericRepository<UserPreference> userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;

        if (userPreferenceRepository.count() == 0) {
            UserPreference userPreference = new UserPreference();
            userPreference.setDiscordUserId("1234");
            userPreference.setPreferredName("Alex");
            userPreferenceRepository.add(userPreference);
        }

        openTelemetry = new FakeOpenTelemetryService();
    }

    public void setPreferredNameForUser(String discordMemberId, String preferredName) {
        var span = openTelemetry.span("setPreferredNameForUser");
        span.setAttribute("discordMemberId", discordMemberId);
        span.setAttribute("preferredName", preferredName);
        try (Scope scope = span.makeCurrent()) {
            UserPreference userPreference = getUserPreferenceForMemberId(discordMemberId);

            userPreference.setPreferredName(preferredName);
            userPreferenceRepository.update(userPreference);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Nullable
    public String getPreferredNameForUser(String discordMemberId) {
        var span = openTelemetry.span("getPreferredNameForUser");
        span.setAttribute("discordMemberId", discordMemberId);
        try (Scope scope = span.makeCurrent()) {
            return getUserPreferenceForMemberId(discordMemberId).getPreferredName();
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Nonnull
    public UserPreference getUserPreferenceForMemberId(String discordMemberId) {
        var span = openTelemetry.span("getUserPreferenceForMemberId");
        span.setAttribute("discordMemberId", discordMemberId);
        try (Scope scope = span.makeCurrent()) {
            Collection<UserPreference> userPreferences = userPreferenceRepository.getAll();
            for (UserPreference currentUserPreference : userPreferences) {
                if (currentUserPreference.getDiscordUserId().equals(discordMemberId)) {
                    return currentUserPreference;
                }
            }

            UserPreference userPreference = new UserPreference();
            userPreference.setDiscordUserId(discordMemberId);
            userPreferenceRepository.add(userPreference);
            return userPreference;
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    /**
     * Given a discord member ID return that user's budget.
     *
     * <p>If no budget has been set, returns null.
     *
     * @param discordMemberId the user to get the budget for
     * @return the user's budget or null if unset
     */
    @Nullable
    public Integer getMaximumBudgetForUser(String discordMemberId) {
        var span = openTelemetry.span("getMaximumBudgetForUser");
        span.setAttribute("discordMemberId", discordMemberId);
        try (Scope scope = span.makeCurrent()) {
            return getUserPreferenceForMemberId(discordMemberId).getMaximumBudget();
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    /**
     * Given a discord member ID set that user's budget
     *
     * @param discordMemberId the user to set the budget for
     * @param maximumBudget the user's budget; must be greater than or equal to 0
     */
    public void setMaximumBudgetForUser(
            @Nonnull String discordMemberId, @Nonnegative @Nonnull Integer maximumBudget) {
        var span = openTelemetry.span("setMaximumBudgetForUser");
        span.setAttribute("discordMemberId", discordMemberId);
        span.setAttribute("maximumBudget", maximumBudget);
        try (Scope scope = span.makeCurrent()) {
            if (discordMemberId == null) {
                throw new IllegalArgumentException("discordMemberId");
            }

            if (maximumBudget == null || maximumBudget < 0) {
                throw new IllegalArgumentException("maximumBudget");
            }

            UserPreference userPreference = getUserPreferenceForMemberId(discordMemberId);

            userPreference.setMaximumBudget(maximumBudget);
            userPreferenceRepository.update(userPreference);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
