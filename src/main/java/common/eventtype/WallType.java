package common.eventtype;

import java.awt.Dimension;

import common.data.MouseData;

public enum WallType {
    NORTH {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() <= WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() > WALL_COOL_TIME_RANGE;
        }

        @Override
        public WallType getOppositeWall() {
            return SOUTH;
        }

        @Override
        public MouseData getLocate(MouseData mouseData, Dimension SCREEN_SIZE) {
            return new MouseData(clamp(mouseData.getMouseX(), 0, SCREEN_SIZE.width - 1), 0);
        }
    },
    SOUTH {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() >= SCREEN_SIZE.height - WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() < SCREEN_SIZE.height - WALL_COOL_TIME_RANGE;
        }

        @Override
        public WallType getOppositeWall() {
            return NORTH;
        }

        @Override
        public MouseData getLocate(MouseData mouseData, Dimension SCREEN_SIZE) {
            return new MouseData(
                    clamp(mouseData.getMouseX(), 0, SCREEN_SIZE.width - 1),
                    SCREEN_SIZE.height - 1);
        }
    },
    WEST {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() <= WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() > WALL_COOL_TIME_RANGE;
        }

        @Override
        public WallType getOppositeWall() {
            return EAST;
        }

        @Override
        public MouseData getLocate(MouseData mouseData, Dimension SCREEN_SIZE) {
            return new MouseData(0, clamp(mouseData.getMouseY(), 0, SCREEN_SIZE.height - 1));
        }
    },
    EAST {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() >= SCREEN_SIZE.width - WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() < SCREEN_SIZE.width - WALL_COOL_TIME_RANGE;
        }

        @Override
        public WallType getOppositeWall() {
            return WEST;
        }

        @Override
        public MouseData getLocate(MouseData mouseData, Dimension SCREEN_SIZE) {
            return new MouseData(
                    SCREEN_SIZE.width - 1,
                    clamp(mouseData.getMouseY(), 0, SCREEN_SIZE.height - 1));
        }
    };

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public abstract boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE);

    public abstract boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE);

    public abstract WallType getOppositeWall();

    public abstract MouseData getLocate(MouseData mouseData, Dimension SCREEN_SIZE);
}
