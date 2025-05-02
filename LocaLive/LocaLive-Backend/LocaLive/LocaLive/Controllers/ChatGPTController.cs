using LocaLive.Class;
using LocaLive.Interfaces;
using LocaLive.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using System;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace LocaLive.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ChatGPTController : ControllerBase
    {
        private readonly IEventRepository _eventRepository;
        private readonly ChatGPTService _chatGPTService;
        private readonly TicketmasterService _ticketmasterService;

        public ChatGPTController(
            IEventRepository eventRepository,
            ChatGPTService chatGPTService,
            TicketmasterService ticketmasterService)
        {
            _eventRepository = eventRepository;
            _chatGPTService = chatGPTService;
            _ticketmasterService = ticketmasterService;
        }

        [HttpGet("three-events-with-comment/{userId}")]
        public async Task<IActionResult> GetThreeEventIdsAndTransferWithComment(int userId)
        {
            try
            {
                
                var userEvents = _eventRepository.GetEventsByUserId(userId).ToList();
                if (!userEvents.Any())
                {
                    return NotFound("No events found for this user.");
                }

                
                var filteredEvents = userEvents.Select(e => new
                {
                    e.EventId,
                    e.EventName,
                    e.RequestGroupId
                }).ToList();

                // json formatına dönüştür
                var eventsJson = JsonConvert.SerializeObject(filteredEvents);

                
                var prompt = $@"
Aşağıda kullanıcıya ait tüm etkinliklerin listesi (JSON) bulunmaktadır. 
Her etkinliğin bir RequestGroupId değeri vardır. Etkinlikleri seçerken farklı RequestGroupId olmasına dikkat et
Aynı eventname ye sahip etkinlikleri verme. farklı olsun.

Lütfen sadece 3 tane etkinlik seç. Eğer 3 ten az etkinlik varsa; kaç tane etkinlik olduğu fark etmez olan etkinlikleri gene sadece bana json verisi ile döndür. 
Bu 3 etkinliğin mutlaka birbirinden farklı RequestGroupId değerleri olsun.
Aynı etkinlikleri gösterme.
Seçeceğin etkinlikler için, birer cümlelik yorum üret.

Kullanılacak JSON dönüş formatı HARİÇ hiçbir ek açıklama yapma. 
Sadece şu formatta dön:
{{
   ""recommendations"": [EtkinlikID1, EtkinlikID2, EtkinlikID3],
   ""comments"": [
     {{ ""EventId"": EtkinlikID1, ""Comment"": ""..."" }},
     {{ ""EventId"": EtkinlikID2, ""Comment"": ""..."" }},
     {{ ""EventId"": EtkinlikID3, ""Comment"": ""..."" }}
   ]
}}



Etkinlikler (JSON):
{eventsJson}
";


              
                var gptResponse = await _chatGPTService.GetSuggestionsAsync(prompt);

                
                GptResponse parsedResponse;
                try
                {
                    parsedResponse = JsonConvert.DeserializeObject<GptResponse>(gptResponse);
                }
                catch (Exception)
                {
                    return BadRequest($"Parse edilemedi {gptResponse}");
                }

                if (parsedResponse == null ||
                    parsedResponse.Recommendations == null ||
                    !parsedResponse.Recommendations.Any())
                {
                    return BadRequest("gpt öneri sunamdı");
                }

                
                var eventIds = parsedResponse.Recommendations;

               
             
               
                var transferResult = await _ticketmasterService.TransferEventsToTripsAsync(eventIds, parsedResponse.Comments);

                
                return Ok(new
                {
                    ChatGPTResponse = gptResponse,
                    TransferResult = transferResult
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"An error occurred: {ex.Message}");
            }
        }
    }
}

