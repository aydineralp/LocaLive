using LocaLive.Class;
using LocaLive.Interfaces;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;

namespace LocaLive.Services
{
    public class TicketmasterService
    {
        private readonly HttpClient _httpClient;
        private readonly ILocationRepository _locationRepository;
        private readonly IEventRepository _eventRepository;
        private readonly IUserRepository _userRepository;
        private readonly ChatGPTService _chatGPTService;
        private readonly ITripRepository _tripRepository;
        private readonly string _apiKey = "******************************";
        private readonly string _baseUrl = "https://app.ticketmaster.com/discovery/v2/events.json?";
        private int _requestGroupId = 0; 

        public TicketmasterService(
            HttpClient httpClient,
            ITripRepository tripRepository,
            ILocationRepository locationRepository,
            IEventRepository eventRepository,
            IUserRepository userRepository,
            ChatGPTService chatGPTService)
        {
            _httpClient = httpClient;
            _tripRepository = tripRepository;
            _locationRepository = locationRepository;
            _eventRepository = eventRepository;
            _userRepository = userRepository;
            _chatGPTService = chatGPTService;
        }

        
        public async Task<string> GetEventsAsync(int userId, DateTime startDateTime, DateTime endDateTime)
        {
            try
            {
                var startDate = startDateTime.ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ");
                var endDate = endDateTime.ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ");

                var user = _userRepository.GetUserById(userId);
                if (user == null)
                    return "Error: User not found";

                var tercih = user.Preferences;
                var location = _locationRepository.GetLocationById(userId);
                if (location == null)
                    return "Error: Location not found";

                bool isAutoDetected = location.IsAutoDetected;
                var tercihDizisi = tercih.Split(',', StringSplitOptions.RemoveEmptyEntries);

                int totalEventCount = 0;

                foreach (var tercihItemRaw in tercihDizisi)
                {
                    _requestGroupId++;
                    var tercihItem = tercihItemRaw.Trim();

                    string url;
                    if (tercihItem.Equals("stand-up", StringComparison.OrdinalIgnoreCase))
                    {
                        // stand-up için keyword kullanıyoruz
                        if (!isAutoDetected)
                        {
                            var sehir = location.City;
                            url = $"{_baseUrl}keyword={tercihItem}&city={sehir}&apikey={_apiKey}&startDateTime={startDate}&endDateTime={endDate}";
                        }
                        else
                        {
                            var koordinat = $"{Math.Round(location.Latitude, 6, MidpointRounding.AwayFromZero).ToString(CultureInfo.InvariantCulture)}," +
                                            $"{Math.Round(location.Longitude, 6, MidpointRounding.AwayFromZero).ToString(CultureInfo.InvariantCulture)}";
                            url = $"{_baseUrl}keyword={tercihItem}&geoPoint={koordinat}&radius=25&unit=km&apikey={_apiKey}&startDateTime={startDate}&endDateTime={endDate}";
                        }
                    }
                    else
                    {
                        // Diğer durumlar için classificationName
                        if (!isAutoDetected)
                        {
                            var sehir = location.City;
                            url = $"{_baseUrl}classificationName={tercihItem}&city={sehir}&apikey={_apiKey}&startDateTime={startDate}&endDateTime={endDate}";
                        }
                        else
                        {
                            var koordinat = $"{Math.Round(location.Latitude, 6, MidpointRounding.AwayFromZero).ToString(CultureInfo.InvariantCulture)}," +
                                            $"{Math.Round(location.Longitude, 6, MidpointRounding.AwayFromZero).ToString(CultureInfo.InvariantCulture)}";
                            url = $"{_baseUrl}classificationName={tercihItem}&geoPoint={koordinat}&radius=25&unit=km&apikey={_apiKey}&startDateTime={startDate}&endDateTime={endDate}";
                        }
                    }

                    var response = await _httpClient.GetAsync(url);
                    if (response.IsSuccessStatusCode)
                    {
                        var content = await response.Content.ReadAsStringAsync();
                        totalEventCount += await SaveEventsFromJsonAsync(content, userId, _requestGroupId);
                    }
                }

                // Kullanıcının EventRange alanını güncelle
                var userToUpdate = _userRepository.GetUserById(userId);
                if (userToUpdate != null)
                {
                    userToUpdate.EventRange = totalEventCount.ToString();
                    _userRepository.UpdateUser(userId, userToUpdate);
                }

                return "Events fetched and saved successfully.";
            }
            catch (Exception ex)
            {
                return $"An error occurred while fetching events: {ex.Message}";
            }
        }

        
        /// Ticketmaster'dan gelen JSON string'i parse edip Event tablosuna ekler.
        
        private async Task<int> SaveEventsFromJsonAsync(string jsonContent, int userId, int requestGroupId)
        {
            int newEventCount = 0;

            try
            {
                var jObject = JObject.Parse(jsonContent);
                var eventsToken = jObject["_embedded"]?["events"];
                if (eventsToken == null)
                    return newEventCount;

                foreach (var item in eventsToken)
                {
                    string eventName = item["name"]?.ToString();
                    string eventUrl = item["url"]?.ToString();
                    string dateTimeString = item["dates"]?["start"]?["dateTime"]?.ToString();

                    DateTime eventDate;
                    if (!DateTime.TryParse(dateTimeString, out eventDate))
                    {
                        var localDateString = item["dates"]?["start"]?["localDate"]?.ToString();
                        var localTimeString = item["dates"]?["start"]?["localTime"]?.ToString();

                        if (!string.IsNullOrWhiteSpace(localDateString) &&
                            !string.IsNullOrWhiteSpace(localTimeString))
                        {
                            if (!DateTime.TryParse($"{localDateString} {localTimeString}", out eventDate))
                            {
                                continue;
                            }
                        }
                        else
                        {
                            continue;
                        }
                    }

                    var venue = item["_embedded"]?["venues"]?.FirstOrDefault();
                    if (venue == null)
                        continue;

                    string addressLine = venue["name"]?.ToString();
                    string latStr = venue["location"]?["latitude"]?.ToString();
                    string lonStr = venue["location"]?["longitude"]?.ToString();

                    decimal latitude, longitude;
                    if (!decimal.TryParse(latStr, NumberStyles.Any, CultureInfo.InvariantCulture, out latitude))
                        latitude = 0;
                    if (!decimal.TryParse(lonStr, NumberStyles.Any, CultureInfo.InvariantCulture, out longitude))
                        longitude = 0;

                    var newEvent = new Event
                    {
                        EventName = eventName,
                        EventDescription = addressLine,
                        EventUrl = eventUrl,
                        Latitude = latitude,
                        Longitude = longitude,
                        EventDate = eventDate,
                        UserId = userId,
                        RequestGroupId = requestGroupId
                    };

                    _eventRepository.AddEvent(newEvent);
                    newEventCount++;
                }

                return newEventCount;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error while saving events: {ex.Message}");
                return newEventCount;
            }
        }

       

        
        /// ChatGPT veya başka bir yerden gelen EventId listesini Trip tablosuna aktarır.
        
        public async Task<string> TransferEventsToTripsAsync(List<int> eventIds, List<GptEventComment> comments = null)
        {
            var trips = new List<Trip>();

            foreach (var eventId in eventIds)
            {
                // Event'i bul
                var eventItem = _eventRepository.GetEventById(eventId);
                if (eventItem == null)
                {
                    return $"Event with ID {eventId} not found.";
                }

                // İlgili comment'i yakala
                // Eğer comment listesi null veya boş ise, varsayılan  değeri kullan
                var comment = comments?
                    .FirstOrDefault(c => c.EventId == eventId)?.Comment
                    ?? string.Empty;

                // Trip nesnesi oluştur
                var newTrip = new Trip
                {
                    TripName = eventItem.EventName,
                    TripDescription = eventItem.EventDescription,
                    TripUrl = eventItem.EventUrl,
                    Latitude = eventItem.Latitude,
                    Longitude = eventItem.Longitude,
                    TripDate = eventItem.EventDate,
                    UserId = eventItem.UserId,
                    RequestGroupId = eventItem.RequestGroupId,

                    // Yeni eklenen alan:
                    TripComment = comment
                };

                trips.Add(newTrip);
            }

            // Hepsini kaydet
            foreach (var trip in trips)
            {
                _tripRepository.AddTrip(trip);
            }

            return "Selected events have been successfully transferred to trips.";
        }

    }
}
