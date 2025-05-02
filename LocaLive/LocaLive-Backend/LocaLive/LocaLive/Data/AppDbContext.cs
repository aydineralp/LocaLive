using LocaLive.Class;
using Microsoft.EntityFrameworkCore;


namespace LocaLive.Context
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions options) : base(options) { }
        public DbSet<Event> Events { get; set; }
        public DbSet<Location> Locations { get; set; }  
        public DbSet<Trip> Trips { get; set; }
        public DbSet<User> Users { get; set; }


    }
}
