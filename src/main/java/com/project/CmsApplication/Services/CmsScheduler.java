package com.project.CmsApplication.Services;


import com.project.CmsApplication.controller.UtilityController;
import com.project.CmsApplication.model.Configuration;
import com.project.CmsApplication.model.Playlist;
import com.project.CmsApplication.model.Promo;
import com.project.CmsApplication.repository.ConfigurationRepository;
import com.project.CmsApplication.repository.PlaylistRepository;
import com.project.CmsApplication.repository.PromoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CmsScheduler {
    @Autowired
    PromoRepository promoRepository;
    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    ConfigurationRepository configurationRepository;

    Logger logger = LoggerFactory.getLogger(CmsScheduler.class);

//    @Scheduled(cron = "0 59 23 * * *")
//    public void promoScheduler() {
//        List<Promo> expiredPromo = promoRepository.getExpiredPromoId();
//        for (Promo promo : expiredPromo) {
//            promoRepository.updatePromo(promo.getBranch_id(), promo.getRegion_id(),
//                    promo.getCompany_id(), promo.getTittle(), promo.getFile(), promo.getDescription(), promo.getPopup(),
//                    promo.getPopup_description(), promo.getStart_date().toString(), promo.getEnd_date().toString(), "inactive", "SYSTEM-AUTO",
//                    promo.getPromo_id(), promo.getThumbnail());
//            logger.info("Promo : " + promo.getTittle() + " has ended");
//        }
//
//    }

    @Scheduled(cron = "0 49 23 * * *")
    public void playlistScheduler() {
        List<Playlist> expiredPlaylist = playlistRepository.getExpiredPlaylist();
        List<Configuration> configurationList = configurationRepository.findAll();
        String scheduler_configuration = "";

        for (Configuration configuration : configurationList) {
            if (configuration.getConfiguration_name().compareToIgnoreCase("scheduler") == 0) {
                scheduler_configuration = configuration.getConfiguration_value();
            }
        }
        if (scheduler_configuration.compareToIgnoreCase("off") == 0 || scheduler_configuration.isEmpty()) {
            logger.info("Scheduler for playlist : " + scheduler_configuration);
            return;
        }
        logger.info("Scheduler for playlist : " + scheduler_configuration);
        logger.info("Starting scheduler for playlist checking");
        String playlist_name;
        String start_date;
        String end_date;
        int playlist_id;
        boolean is_default;
        for (Playlist playlist : expiredPlaylist) {
            playlist_name = playlist.getPlaylist_name();
            start_date = playlist.getStart_date().toString();
            end_date = playlist.getEnd_date().toString();
            playlist_id = playlist.getPlaylist_id();
            is_default = playlist.isIs_default();
            playlistRepository.updatePlaylist(playlist_name, start_date, end_date, "inactive", is_default,
                    "SYSTEM-AUTO", playlist_id);
            logger.info("Playlist : " + playlist.getPlaylist_name() + " has ended");
        }
    }

}
