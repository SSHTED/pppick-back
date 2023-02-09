package com.pickx3.domain.repository;

import com.pickx3.domain.entity.Favorites;
import com.pickx3.domain.entity.portfolio_package.Portfolio;
import com.pickx3.domain.entity.user_package.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorites, Long> {

    Optional<Favorites> findByUserAndPortfolio(User user, Portfolio portfolio);
}
