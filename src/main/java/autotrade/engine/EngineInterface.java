package autotrade.engine;

import autotrade.engine.reactive.EngineExeption;

import java.sql.SQLException;

public interface EngineInterface {

    public void loop() throws SQLException, EngineExeption;
    public void init() throws EngineExeption, SQLException;
}
