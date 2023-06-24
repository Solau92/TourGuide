package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tourGuide.dto.NearByAttractionDto;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.implementation.GpsServiceImpl;
import tourGuide.service.implementation.TourGuideServiceImpl;
import tourGuide.service.implementation.UserServiceImpl;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	private Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	private TourGuideServiceImpl tourGuideServiceImpl;

	private UserServiceImpl userService;

	private GpsServiceImpl gpsService;

	public TourGuideController(TourGuideServiceImpl tourGuideServiceImpl, UserServiceImpl userService, GpsServiceImpl gpsService) {
		this.userService = userService;
		this.tourGuideServiceImpl = tourGuideServiceImpl;
		this.gpsService = gpsService;
	}

	@RequestMapping("/")
	public String index() {
		return "Greetings from TourGuide!";
	}

	@RequestMapping("/getLocation")
	public ResponseEntity<Location> getLocation(@RequestParam String userName) throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tourGuideServiceImpl.getUserLocation(userName));
	}

	//  TodoNearlyDone : reward points
	//  Note: Attraction reward points can be gathered from RewardsCentral
	@RequestMapping("/getNearbyAttractions")
	public ResponseEntity<List<NearByAttractionDto>> getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ? UserService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getNearbyAttractions(userName));
	}

	// Added to test
	@RequestMapping("/getAllAttractions")
	public ResponseEntity<List<Attraction>> getNearbyAttractions() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(gpsService.getAllAttractions());
	}

	@RequestMapping("/getRewards")
	public ResponseEntity<List<UserReward>> getRewards(@RequestParam String userName) throws UserNotFoundException {
		// A mettre dans TourGuideService ?? gpsService ? UserService ??
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRewards(userName));
	}

	@RequestMapping("/getAllCurrentLocations")
	public ResponseEntity<Map<UUID, Location>> getAllCurrentLocations() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getAllCurrentLocations());
	}

	@RequestMapping("/getTripDeals")
	public ResponseEntity<List<Provider>> getTripDeals(@RequestParam String userName) throws UserNotFoundException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getTripDeals(userName));
	}

	// Added to test
	@PostMapping("/addUser")
	public ResponseEntity<User> addUser(@RequestBody User user) throws UserAlreadyExistsException {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(user).get());
	}

}