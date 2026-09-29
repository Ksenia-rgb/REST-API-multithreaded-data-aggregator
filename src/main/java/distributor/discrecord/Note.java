package distributor.discrecord;

import com.fasterxml.jackson.databind.JsonNode;

public record Note(long id, String source, String timestamp, JsonNode data){}
