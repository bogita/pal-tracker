package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTimeEntryRepository(MysqlDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        try {
            KeyHolder holder = new GeneratedKeyHolder();
            int insertFlag = getJdbcTemplate().update( con -> {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) VALUES(?,?,?,?)",
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, (int) timeEntry.getProjectId());
                        ps.setInt(2, (int) timeEntry.getUserId());
                        ps.setDate(3, Date.valueOf(timeEntry.getDate()));
                        ps.setInt(4, timeEntry.getHours());
                        return ps;
                    }, holder);
            timeEntry.setId(holder.getKey().intValue());

            return insertFlag == 1 ? timeEntry : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public TimeEntry find(long id) {
        try {

            return getJdbcTemplate().queryForObject("SELECT * FROM time_entries WHERE id=?",
                        new Object[]{(int) id},
                        new BeanPropertyRowMapper<>(TimeEntry.class));

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        return getJdbcTemplate().query("Select * from time_entries", (rs, rowNum) -> {
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setId(rs.getLong("id"));
            timeEntry.setProjectId(rs.getLong("project_id"));
            timeEntry.setUserId(rs.getLong("user_id"));
            timeEntry.setDate(rs.getDate("date").toLocalDate());
            timeEntry.setHours(rs.getInt("hours"));
            return timeEntry;
        });
    }

    @Override
    public TimeEntry update(long id, TimeEntry entry) {
        entry.setId(id);
        SqlParameterSource params = new BeanPropertySqlParameterSource(entry);
        String sql = "UPDATE time_entries SET user_id=:userId, project_id=:projectId, date=:date, hours=:hours WHERE id=:id";
        int updateCount = getNamedParameterJdbcTemplate().update(sql, params);
        return updateCount > 0? entry: null;
    }

    @Override
    public void delete(long id) {
        getJdbcTemplate().update("DELETE FROM time_entries WHERE id =? ", (int)id);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }
}
