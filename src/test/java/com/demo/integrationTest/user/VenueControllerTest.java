package com.demo.integrationTest.user;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VenueService venueService;

    @Test
    public void testVenuePage() throws Exception {
        Venue venue = new Venue();
        when(venueService.findByVenueID(1)).thenReturn(venue);

        mockMvc.perform(get("/venue")
                        .param("venueID", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("venue"));
    }

    @Test
    public void testVenueListPage() throws Exception {
        Page<Venue> page = new PageImpl<>(Collections.singletonList(new Venue()));
        when(venueService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/venue_list"))
                .andExpect(status().isOk())
                .andExpect(view().name("venue_list"));
    }

    @Test
    public void testGetVenueList() throws Exception {
        Page<Venue> page = new PageImpl<>(Collections.singletonList(new Venue()));
        when(venueService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/venuelist/getVenueList")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
}
