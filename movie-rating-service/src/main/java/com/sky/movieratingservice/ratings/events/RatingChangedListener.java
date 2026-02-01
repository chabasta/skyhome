package com.sky.movieratingservice.ratings.events;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RatingChangedListener {

    private final CacheManager cacheManager;

    public RatingChangedListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @EventListener
    public void onRatingChanged(RatingChangedEvent event) {
        Cache cache = cacheManager.getCache("topRatedMovie");
        if (cache != null) {
            cache.clear();
        }
    }
}

