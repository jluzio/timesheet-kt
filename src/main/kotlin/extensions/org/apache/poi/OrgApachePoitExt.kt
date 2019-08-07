package extensions.org.apache.poi

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellReference

fun Sheet.findCell(row: Int, column: Int): Cell = getRow(row).getCell(column)

fun Cell.getRef(): String = CellReference(this).formatAsString()

/* Builders */
fun Sheet.row(row: Int, init: Row.() -> Unit): Row = createRow(row).apply(init)

fun Row.cell(column: Int, cellStyle: CellStyle? = null, init: Cell.() -> Unit): Cell = createCell(column).apply{
    cellStyle?.let { this.cellStyle = it }
    init(this)
}

fun CellStyle.copyStyle(otherCellStyle: CellStyle?): CellStyle = apply { this.cloneStyleFrom(otherCellStyle) }
