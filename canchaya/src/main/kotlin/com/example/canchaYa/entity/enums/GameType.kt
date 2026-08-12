package com.example.canchaYa.entity.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class GameType {
    SUPER_8,
    @JsonProperty("CLASSIC")
    TRADITIONAL
}
