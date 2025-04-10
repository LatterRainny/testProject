package com.demo.integrationTest.admin;

import com.demo.entity.Venue;
import com.demo.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminVenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VenueService venueService;

    @Test
    public void testVenueManage() throws Exception {
        Page<Venue> page = new PageImpl<>(Collections.singletonList(new Venue()));
        when(venueService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/venue_manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venue_manage"));

        verify(venueService, times(1)).findAll(any());
    }

    @Test
    public void testEditVenue() throws Exception {
        Venue venue = new Venue();
        when(venueService.findByVenueID(1)).thenReturn(venue);

        mockMvc.perform(get("/venue_edit")
                        .param("venueID", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/venue_edit"));

        verify(venueService, times(1)).findByVenueID(1);
    }

    @Test
    public void testVenueAdd() throws Exception {
        mockMvc.perform(get("/venue_add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/venue_add"));
    }

    @Test
    public void testGetVenueList() throws Exception {
        Page<Venue> page = new PageImpl<>(Collections.singletonList(new Venue()));
        when(venueService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/venueList.do")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(venueService, times(1)).findAll(any());
    }

    @Test
    public void testCheckVenueName() throws Exception {
        when(venueService.countVenueName("test")).thenReturn(0);

        mockMvc.perform(post("/checkVenueName.do")
                        .param("venueName", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(venueService, times(1)).countVenueName("test");
    }

    @Test
    public void testCheckVenueNameExist() throws Exception {
        when(venueService.countVenueName("test")).thenReturn(1);
        mockMvc.perform(post("/checkVenueName.do")
                        .param("venueName", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
        verify(venueService, times(1)).countVenueName("test");
    }

    @Test
    public void testDelVenue() throws Exception {
        mockMvc.perform(post("/delVenue.do")
                        .param("venueID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}