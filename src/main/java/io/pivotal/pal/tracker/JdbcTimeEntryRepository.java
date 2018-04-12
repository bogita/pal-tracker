package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(MysqlDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        try {

            KeyHolder holder = new GeneratedKeyHolder();
            int insertFlag = getJdbcTemplate().update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement ps = con.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) VALUES(?,?,?,?)",
                                    Statement.RETURN_GENERATED_KEYS);
                            ps.setInt(1, (int) timeEntry.getProjectId());
                            ps.setInt(2, (int) timeEntry.getUserId());
                            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
                            ps.setInt(4, timeEntry.getHours());
                            return ps;
                        }
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
            return (TimeEntry)getJdbcTemplate().queryForObject("SELECT * FROM time_entries WHERE id=?",
                    new Object[]{(int) id},
                    new BeanPropertyRowMapper(TimeEntry.class));

        }catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        return getJdbcTemplate().query("Select * from time_entries", new RowMapper<TimeEntry>() {
            @Override
            public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setId(rs.getLong("id"));
                timeEntry.setProjectId(rs.getLong("project_id"));
                timeEntry.setUserId(rs.getLong("user_id"));
                timeEntry.setDate(rs.getDate("date").toLocalDate());
                timeEntry.setHours(rs.getInt("hours"));
                return timeEntry;
            }
        });
    }

    @Override
    public TimeEntry update(long id, TimeEntry entry) {
        int flag = getJdbcTemplate()
                .update("UPDATE time_entries SET user_id=?, project_id=?, date=?, hours=? WHERE id=?",
                        (int)entry.getUserId(), (int)entry.getProjectId(), Date.valueOf(entry.getDate()), entry.getHours(), (int)id);
        entry.setId(id);
        return flag > 0? entry: null;
    }

    @Override
    public void delete(long id) {
        getJdbcTemplate().update("DELETE FROM time_entries WHERE id =? ", (int)id);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
