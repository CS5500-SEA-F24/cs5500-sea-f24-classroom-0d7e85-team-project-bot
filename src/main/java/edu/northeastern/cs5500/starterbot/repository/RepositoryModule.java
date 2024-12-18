package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.UserPreference;

@Module
public class RepositoryModule {
    @Provides
    public GenericRepository<UserPreference> provideUserPreferencesRepository(
            MongoDBRepository<UserPreference> repository) {
        return repository;
    }

    @Provides
    public Class<UserPreference> provideUserPreference() {
        return UserPreference.class;
    }
}
