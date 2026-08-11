package com.example.canchaYa.service

import com.example.canchaYa.dto.GameRecordRequest
import com.example.canchaYa.dto.PlayerRequest
import com.example.canchaYa.dto.ReservationRequest
import com.example.canchaYa.entity.Court
import com.example.canchaYa.entity.Slot
import com.example.canchaYa.entity.enums.GameType
import com.example.canchaYa.entity.enums.Team
import com.example.canchaYa.entity.BaseEntity
import com.example.canchaYa.repository.*
import com.example.canchaYa.service.GameRecordService
import com.example.canchaYa.service.PlayerService
import com.example.canchaYa.service.ReservationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class ReservationAndGameRecordServiceTest {

    @Autowired
    lateinit var reservationService: ReservationService

    @Autowired
    lateinit var gameRecordService: GameRecordService

    @Autowired
    lateinit var playerService: PlayerService

    @Autowired
    lateinit var courtRepository: CourtRepository

    @Autowired
    lateinit var slotRepository: SlotRepository

    @Autowired
    lateinit var reservationRepository: ReservationRepository

    @Autowired
    lateinit var gameRecordRepository: GameRecordRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    lateinit var testCourt: Court
    lateinit var slot1: Slot
    lateinit var slot2: Slot
    lateinit var slot3: Slot

    @BeforeEach
    fun setUp() {
        playerRepository.deleteAll()
        gameRecordRepository.deleteAll()
        reservationRepository.deleteAll()
        slotRepository.deleteAll()
        courtRepository.deleteAll()

        testCourt = courtRepository.save(Court(name = "Cancha Central", isIndoor = true))
        val now = LocalDateTime.now()
        slot1 = slotRepository.save(Slot(court = testCourt, startTime = now, endTime = now.plusHours(1), price = BigDecimal("10.00")))
        slot2 = slotRepository.save(Slot(court = testCourt, startTime = now.plusHours(1), endTime = now.plusHours(2), price = BigDecimal("12.00")))
        slot3 = slotRepository.save(Slot(court = testCourt, startTime = now.plusHours(2), endTime = now.plusHours(3), price = BigDecimal("15.00")))
    }

    @Test
    fun `SUPER_8 reservation must have exactly 2 slot IDs`() {
        // Valid reservation
        val validRequest = ReservationRequest(slotIds = listOf(slot1.id, slot2.id), gameType = GameType.SUPER_8)
        val validReservation = reservationService.createReservation(validRequest, "user-123")
        assertNotNull(validReservation)

        // Invalid reservation - 1 slot
        val invalidRequest1 = ReservationRequest(slotIds = listOf(slot1.id), gameType = GameType.SUPER_8)
        val exception1 = assertThrows(IllegalArgumentException::class.java) {
            reservationService.createReservation(invalidRequest1, "user-123")
        }
        assertEquals("SUPER_8 game type requires exactly 2 slot IDs", exception1.message)

        // Invalid reservation - 3 slots
        val invalidRequest3 = ReservationRequest(slotIds = listOf(slot1.id, slot2.id, slot3.id), gameType = GameType.SUPER_8)
        val exception3 = assertThrows(IllegalArgumentException::class.java) {
            reservationService.createReservation(invalidRequest3, "user-123")
        }
        assertEquals("SUPER_8 game type requires exactly 2 slot IDs", exception3.message)
    }

    @Test
    fun `TRADITIONAL reservation must have exactly 1 slot ID`() {
        // Valid reservation
        val validRequest = ReservationRequest(slotIds = listOf(slot1.id), gameType = GameType.TRADITIONAL)
        val validReservation = reservationService.createReservation(validRequest, "user-123")
        assertNotNull(validReservation)

        // Invalid reservation - 2 slots
        val invalidRequest2 = ReservationRequest(slotIds = listOf(slot1.id, slot2.id), gameType = GameType.TRADITIONAL)
        val exception2 = assertThrows(IllegalArgumentException::class.java) {
            reservationService.createReservation(invalidRequest2, "user-123")
        }
        assertEquals("TRADITIONAL game type requires exactly 1 slot ID", exception2.message)
    }

    @Test
    fun `TRADITIONAL game record player validations`() {
        val reservationRequest = ReservationRequest(slotIds = listOf(slot1.id), gameType = GameType.TRADITIONAL)
        val reservationDto = reservationService.createReservation(reservationRequest, "user-123")
        
        val gameRecordDto = gameRecordService.createGameRecord(GameRecordRequest(reservationDto.id))
        
        // Cannot add player with Team.NONE
        val noneException = assertThrows(IllegalArgumentException::class.java) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "John", team = Team.NONE))
        }
        assertEquals("Player must be assigned to TEAM_A or TEAM_B", noneException.message)

        // Add 2 players to TEAM_A
        gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A1", team = Team.TEAM_A))
        gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A2", team = Team.TEAM_A))

        // Adding 3rd player to TEAM_A fails
        val teamAException = assertThrows(IllegalArgumentException::class.java) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A3", team = Team.TEAM_A))
        }
        assertTrue(teamAException.message!!.contains("already has the maximum of 2 players"))

        // Add 2 players to TEAM_B
        val b1 = gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player B1", team = Team.TEAM_B))
        gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player B2", team = Team.TEAM_B))

        // Adding 5th player in total fails
        val limitException = assertThrows(IllegalArgumentException::class.java) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A3", team = Team.TEAM_B))
        }
        assertTrue(limitException.message!!.contains("maximum of 4 players"))

        // Update player cannot exceed team capacity
        val updateException = assertThrows(IllegalArgumentException::class.java) {
            playerService.updatePlayer(b1.id, PlayerRequest(playerName = "Player B1", team = Team.TEAM_A), "user-123")
        }
        assertTrue(updateException.message!!.contains("already has the maximum of 2 players"))
    }

    @Test
    fun `SUPER_8 game record player validations`() {
        val reservationRequest = ReservationRequest(slotIds = listOf(slot1.id, slot2.id), gameType = GameType.SUPER_8)
        val reservationDto = reservationService.createReservation(reservationRequest, "user-123")
        
        val gameRecordDto = gameRecordService.createGameRecord(GameRecordRequest(reservationDto.id))

        // Add 4 players to TEAM_A
        for (i in 1..4) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A$i", team = Team.TEAM_A))
        }

        // Adding 5th player to TEAM_A fails
        val teamAException = assertThrows(IllegalArgumentException::class.java) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player A5", team = Team.TEAM_A))
        }
        assertTrue(teamAException.message!!.contains("already has the maximum of 4 players"))

        // Add 4 players to TEAM_B
        val b1 = gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player B1", team = Team.TEAM_B))
        for (i in 2..4) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player B$i", team = Team.TEAM_B))
        }

        // Adding 9th player in total fails
        val limitException = assertThrows(IllegalArgumentException::class.java) {
            gameRecordService.addPlayer(gameRecordDto.id, PlayerRequest(playerName = "Player Extra", team = Team.TEAM_B))
        }
        assertTrue(limitException.message!!.contains("maximum of 8 players"))

        // Update player cannot exceed team capacity
        val updateException = assertThrows(IllegalArgumentException::class.java) {
            playerService.updatePlayer(b1.id, PlayerRequest(playerName = "Player B1", team = Team.TEAM_A), "user-123")
        }
        assertTrue(updateException.message!!.contains("already has the maximum of 4 players"))
    }
}
