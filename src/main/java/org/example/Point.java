package org.example;

    // מחלקת עזר פנימית לייצוג נקודה במטריצה
    public class Point {
        private int row;
        private int col;

        Point(int row, int col) {
            this.row = row;
            this.col = col;
        }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Point point = (Point) o;
//        return row == point.row && col == point.col;
//    }
//
//    @Override
//    public int hashCode() {
//        return 31 * row + col;
//    }

        public int getCol() {
            return col;
        }

        public int getRow() {
            return row;
        }
    }
