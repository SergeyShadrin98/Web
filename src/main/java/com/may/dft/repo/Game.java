package com.may.dft.repo;

import javax.persistence.*;

@Table(name = "Game")
@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;
    @Column(name="price")
    private int    price;
    @Column(name="buy_count")
    private long   buy_count;

    public Game() {}

    public Game(String name, int price)
    {
        this.name  = name;
        this.price = price;
    }



    // SET functions

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public void setBuy_count(long buy_count) {this.buy_count = buy_count;}


    // GET functions

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getPrice()
    {
        return price;
    }

    public long getBuy_count(){return buy_count;}


    @Override
    public String toString()
    {
        return "Game{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", buy_count='" + buy_count + '\'' +
                '}';
    }
}
