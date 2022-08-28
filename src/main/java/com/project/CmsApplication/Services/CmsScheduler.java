package com.project.CmsApplication.Services;


import com.project.CmsApplication.controller.UtilityController;
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

    Logger logger = LoggerFactory.getLogger(CmsScheduler.class);

    @Scheduled(cron = "0 59 23 * * *")
    public void promoScheduler() {
        List<Promo> expiredPromo = promoRepository.getExpiredPromoId();
        for (Promo promo : expiredPromo) {
            promoRepository.updatePromo(promo.getBranch_id(), promo.getRegion_id(),
                    promo.getCompany_id(), promo.getTittle(), promo.getFile(), promo.getDescription(), promo.getPopup(),
                    promo.getPopup_description(), promo.getStart_date().toString(), promo.getEnd_date().toString(), "inactive", "SYSTEM-AUTO",
                    promo.getPromo_id(), promo.getThumbnail());
            logger.info("Promo : " + promo.getTittle() + " has ended");
        }

    }

    @Scheduled(cron = "0 49 23 * * *")
    public void playlistScheduler() {
        List<Playlist> expiredPlaylist = playlistRepository.getExpiredPlaylist();
        for (Playlist playlist : expiredPlaylist) {
            playlistRepository.updatePlaylist(playlist.getPlaylist_name(), playlist.getBranch_id(), playlist.getRegion_id(), playlist.getCompany_id(),
                    playlist.getPosition_id(), playlist.getStart_date().toString(), playlist.getEnd_date().toString(), "inactive", "SYSTEM-AUTO",
                    playlist.getPlaylist_id());
            logger.info("Playlist : " + playlist.getPlaylist_name() + " has ended");
        }
    }

}
