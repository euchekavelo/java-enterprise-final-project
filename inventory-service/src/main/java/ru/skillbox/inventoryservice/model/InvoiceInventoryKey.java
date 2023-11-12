package ru.skillbox.inventoryservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class InvoiceInventoryKey implements Serializable {

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "inventory_id")
    private Long inventoryId;

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceInventoryKey that = (InvoiceInventoryKey) o;
        return Objects.equals(invoiceId, that.invoiceId) && Objects.equals(inventoryId, that.inventoryId);
    }*/

    /*@Override
    public int hashCode() {
        return Objects.hash(invoiceId, inventoryId);
    }*/
}
