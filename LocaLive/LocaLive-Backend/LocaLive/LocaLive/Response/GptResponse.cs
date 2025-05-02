using System.Collections.Generic;

public class GptResponse
{
    public List<int> Recommendations { get; set; }
    public List<GptEventComment> Comments { get; set; }
}

public class GptEventComment
{
    public int EventId { get; set; }
    public string Comment { get; set; }
}
