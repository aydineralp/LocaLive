using System.Text.Json;
using System.Text;
using LocaLive.Class;

public class ChatGPTService
{
    private readonly HttpClient _httpClient;
    private const string ApiKey = "********************";

    public ChatGPTService(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    
    public async Task<string> GetSuggestionsAsync(string prompt)
    {
        var requestContent = new
        {
            model = "gpt-3.5-turbo",
            messages = new[]
            {
                new { role = "system", content = "You are an assistant that filters events based on user preferences." },
                new { role = "user", content = prompt }
            }
        };

        var requestBody = JsonSerializer.Serialize(requestContent);
        var requestMessage = new HttpRequestMessage(HttpMethod.Post, "https://api.openai.com/v1/chat/completions")
        {
            Headers = { { "Authorization", $"Bearer {ApiKey}" } },
            Content = new StringContent(requestBody, Encoding.UTF8, "application/json")
        };

        var response = await _httpClient.SendAsync(requestMessage);
        response.EnsureSuccessStatusCode();

        var responseContent = await response.Content.ReadAsStringAsync();
        var jsonResponse = JsonDocument.Parse(responseContent);

        return jsonResponse.RootElement
                           .GetProperty("choices")[0]
                           .GetProperty("message")
                           .GetProperty("content")
                           .GetString();
    }
}
