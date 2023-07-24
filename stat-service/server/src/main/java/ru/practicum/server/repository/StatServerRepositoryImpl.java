package ru.practicum.server.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.StatHits;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StatServerRepositoryImpl implements StatServerRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public StatServerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public void save(EndpointHit endpointHit) {
        String sqlForGetIdApps = "SELECT apps.id FROM apps WHERE name = ?";
        long appId;

        try {
            appId = jdbcTemplate.queryForObject(sqlForGetIdApps, Long.class, endpointHit.getApp());
        } catch (EmptyResultDataAccessException e) {
            appId = 0;
        }

        if (appId == 0) {
            String sqlForAddApp = "INSERT INTO apps(name) VALUES (?)";
            KeyHolder keyHolder1 = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlForAddApp, new String[]{"id"});
                stmt.setString(1, endpointHit.getApp());
                return stmt;
            }, keyHolder1);

            appId = Objects.requireNonNull(keyHolder1.getKey()).longValue();
        }

        String sql = "INSERT INTO endpoint_hits(app_id, uri, ip, timestamp) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder2 = new GeneratedKeyHolder();
        long finalAppId = appId;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setLong(1, finalAppId);
            stmt.setString(2, endpointHit.getUri());
            stmt.setString(3, endpointHit.getIp());
            stmt.setTimestamp(4, Timestamp.valueOf(endpointHit.getTimestamp()));
            return stmt;
        }, keyHolder2);
    }

    @Override
    public List<StatHits> findAllStatsWithUris(List<String> uris, LocalDateTime start, LocalDateTime end,
                                               boolean unique) {
        SqlParameterSource namedParameters = new MapSqlParameterSource(Map.of(
                "uris", uris,
                "start", start,
                "end", end)
        );

        String sql = "select r.app, r.uri, count(*) hits from (select h.ip, a.name as app, h.uri from endpoint_hits h" +
                " join apps a on h.app_id = a.id where h.uri in (:uris) and" +
                " cast(h.timestamp as date) between cast((:start) as date) and cast((:end) as date)) as r" +
                " group by r.app, r.uri order by hits DESC";

        if (unique) {
            sql = "select r.app, r.uri, count(*) hits from (select h.ip, a.name as app, h.uri from" +
                    " endpoint_hits h join apps a on h.app_id = a.id where h.uri in (:uris) and" +
                    " cast(h.timestamp as date) between cast((:start) as date) and cast((:end) as date)" +
                    " group by h.ip, a.name, h.uri) as r" +
                    " group by r.app, r.uri order by hits DESC";
        }

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeStatResponseDto(rs));
    }

    @Override
    public List<StatHits> findAllStats(LocalDateTime start, LocalDateTime end, boolean unique) {
        SqlParameterSource namedParameters = new MapSqlParameterSource(Map.of(
                "start", start,
                "end", end)
        );

        String sql = "select r.app, r.uri, count(*) hits from (select h.ip, a.name as app, h.uri from endpoint_hits h" +
                " join apps a on h.app_id = a.id " +
                " where cast(h.timestamp as date) between cast((:start) as date) and cast((:end) as date)) as r" +
                " group by r.app, r.uri order by hits DESC";

        if (unique) {
            sql = "select r.app, r.uri, count(*) hits from (select h.ip, a.name as app, h.uri from endpoint_hits h" +
                    " join apps a on h.app_id = a.id" +
                    " where cast(h.timestamp as date) between cast((:start) as date) and cast((:end) as date)" +
                    " group by h.ip, a.name, h.uri) as r" +
                    " group by r.app, r.uri order by hits DESC";
        }

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeStatResponseDto(rs));
    }

    private StatHits makeStatResponseDto(ResultSet rs) throws SQLException {
        return new StatHits(
                rs.getString("app"),
                rs.getString("uri"),
                rs.getLong("hits")
        );
    }
}
