package tourGuide.repository.implementation;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
//import tourGuide.helper.UsersTestConfig;
import tourGuide.helper.UsersTestConfig;
import tourGuide.repository.UserRepository;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;
import tripPricer.Provider;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@DependsOn("usersTestConfig")
public class UserRepositoryImpl implements UserRepository {

	private final Map<String, User> internalUserMap = new HashMap<>();
	Map<String, User> users;
	private Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	public UserRepositoryImpl() {
		this.users = new HashMap<>();
		if (UsersTestConfig.TEST_MODE) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			this.users = UsersTestConfig.initializeInternalUsers();
			logger.info("Finished initializing users");

		}
	}

	@Override
	public Map<String, User> getAllUsers() {
		return users;
	}

	@Override
	public Optional<User> getUserByUserName(String userName) throws UserNotFoundException {

		for (String s : users.keySet()) {
			if (s.equalsIgnoreCase(userName)) {
				return Optional.of(users.get(s));
			}
		}
		logger.error("User with userName " + userName + " was not found");
		return Optional.empty();
	}

	@Override
	public Optional<User> addUser(User user) {

		if (isUserAlreadyRegistered(user)) {
			logger.error("User with userName " + user.getUserName() + " already exists");
			return Optional.empty();
		} else {
			User userSaved = new User(UUID.randomUUID(), user.getUserName(), user.getPhoneNumber(), user.getEmailAddress());
			users.put(userSaved.getUserName(), userSaved);
			//			generateUserLocationHistory(userSaved);
			return Optional.of(userSaved);
		}
	}

	private boolean isUserAlreadyRegistered(User user) {

		return users.containsKey(user.getUserName());

	}

	@Override
	public Optional<Location> getUserLocation(User user) {
		return (user.getVisitedLocations().size() > 0) ?
				Optional.of(user.getLastVisitedLocation().location) : Optional.empty();
	}

	@Override
	public Map<UUID, Location> getAllCurrentLocations() {

		Map<UUID, Location> currentLocations = new HashMap<>();

		for (User u : users.values()) {
			currentLocations.put(u.getUserId(), u.getLastVisitedLocation().location);
		}
		return currentLocations;
	}

	@Override
	public List<UserReward> getUserRewards(String userName) throws UserNotFoundException {
		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getUserRewards();
	}

	@Override
	public List<Provider> getTripDeals(String userName) throws UserNotFoundException {

		if (this.getUserByUserName(userName).isEmpty()) {
			logger.error("User with userName " + userName + " was not found");
			throw new UserNotFoundException("User with userName " + userName + " was not found");
		}
		return this.getUserByUserName(userName).get().getTripDeals();
	}

	@Override
	public UserPreferences setUserPreferences(User user, UserPreferences userPreferences) {
		user.setUserPreferences(userPreferences);
		return user.getUserPreferences();
	}

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");

		this.users = internalUserMap;
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
