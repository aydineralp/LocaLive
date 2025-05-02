namespace LocaLive.Class
{
    public class Trip
    {
        public int TripId { get; set; }
        public string TripName { get; set; }
        public string TripDescription { get; set; }
        public string TripUrl { get; set; }
        public decimal Latitude { get; set; }
        public decimal Longitude { get; set; }
        public DateTime TripDate { get; set; }
        public int UserId { get; set; }
        public int RequestGroupId { get; set; }
        public string TripComment { get; set; }
    }
}
