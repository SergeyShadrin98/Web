package com.may.dft.controller;

import com.may.dft.repo.Game;
import com.may.dft.repo.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ShopController
{
    @Autowired
    ShopRepository shopRespository;

    @GetMapping("/myindex")
    public String main(Model model, HttpServletRequest request, HttpSession session)
    {

        return "myindex";
    }

    @GetMapping("/")
    public String main(Model model, @CookieValue(value = "userLocation", defaultValue = "не опознано.") String myCookie, @RequestParam(required=false) boolean success, HttpServletRequest request, HttpSession session)
    {
        if (success)
        {
            model.addAttribute("result", "Операция прошла успешно.");
        }

        List<String> filteredGames = new ArrayList<>();
        List<String> filteredPrices = new ArrayList<>();

        for (int i = 1; i <= shopRespository.count(); i++)
        {
            filteredGames.add(shopRespository.getOne(i).getName());
            filteredPrices.add(String.valueOf( shopRespository.getOne(i).getPrice() ));
        }

        int cartCount = 0;
        List<Integer> gamesInCart = new ArrayList<>();
        Object o_cartCount = request.getSession().getAttribute("cartcount");
        if (o_cartCount == null)
        {
            session.setAttribute("cartcount", cartCount);
            session.setAttribute("games", gamesInCart);
        }
        else
        {
            cartCount = (int)o_cartCount;
        }

        String location = myCookie;

        model.addAttribute("location", location);
        model.addAttribute("games", filteredGames);
        model.addAttribute("prices", filteredPrices);
        model.addAttribute("cartcount", Integer.toString(cartCount));

        return "index";
    }

    @GetMapping("/setSpot/{location}")
    public String setSpot(HttpServletResponse response, @PathVariable("location") String location)
    {
        Cookie cookie = new Cookie("userLocation", location);
        cookie.setMaxAge(60 * 60); //set expire time to 60*60 sec
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);

        return "redirect:/";
    }


    @GetMapping("/checkout")
    public String checkout(Model model, HttpServletRequest request, HttpSession session)
    {
        int cartCount = 0;
        Object o_cartCount = request.getSession().getAttribute("cartcount");
        if (o_cartCount == null)
        {
            List<Integer> gamesInCart = new ArrayList<>();

            session.setAttribute("cartcount", cartCount);
            session.setAttribute("games", gamesInCart);

            Integer finalPrice = 0;

            model.addAttribute("cartcount", cartCount);
            model.addAttribute("finalprice", finalPrice);

            return "checkout";
        }
        else
        {
            cartCount = (int)o_cartCount;

            List<Integer> gamesInCart = new ArrayList<>();
            gamesInCart = (List<Integer>)request.getSession().getAttribute("games");

            List<String> games1 = new ArrayList<>();
            List<Integer> gamesids = new ArrayList<>();

            for (int i = 0; i < gamesInCart.size(); i++)
            {
                // games1.add( games.get( gamesInCart.get(i) ) );
                games1.add( shopRespository.getOne( gamesInCart.get(i) ).getName() );
                gamesids.add( gamesInCart.get(i) );
            }



            Integer finalPrice = 0;

            List<Integer> prices1 = new ArrayList<>();
            for (int i = 0; i < gamesInCart.size(); i++)
            {
                //prices1.add( Integer.parseInt( prices.get(gamesInCart.get(i)) ) );
                prices1.add( shopRespository.getOne( gamesInCart.get(i)).getPrice() );

                //finalPrice += Integer.parseInt(prices.get(gamesInCart.get(i)));
                finalPrice += shopRespository.getOne( gamesInCart.get(i)).getPrice();
            }



            model.addAttribute("games", games1);
            model.addAttribute("gameids", gamesids);
            model.addAttribute("prices", prices1);
            model.addAttribute("cartcount", Integer.toString(cartCount));
            model.addAttribute("finalprice", finalPrice);

            return "checkout";
        }
    }

    @GetMapping("/buy")
    public String buy(Model model, HttpServletRequest request, HttpSession session)
    {
        String result = "";

        int cartCount = 0;
        Object o_cartCount = request.getSession().getAttribute("cartcount");
        if (o_cartCount != null)
        {
            List<Integer> gamesInCart1 = new ArrayList<>();
            gamesInCart1 = (List<Integer>)request.getSession().getAttribute("games");

            for (int i = 0; i < gamesInCart1 .size(); i++)
            {
                shopRespository.getOne(gamesInCart1.get(i)).setBuy_count(
                        shopRespository.getOne( gamesInCart1.get(i) ).getBuy_count() + 1
                );

                shopRespository.save(shopRespository.getOne(gamesInCart1.get(i)));
            }



            cartCount = (int)o_cartCount;
            cartCount = 0;

            List<Integer> gamesInCart = new ArrayList<>();

            session.setAttribute("cartcount", cartCount);
            session.setAttribute("games", gamesInCart);

            return "redirect:/?success=true";
        }
        else
        {
            return "redirect:/?success=false";
        }
    }

    @PostMapping("checkout/erase/{gameId}")
    public @ResponseBody String deleteGameFromCart(@PathVariable("gameId") int id, HttpServletRequest request, HttpSession session)
    {
        int cartCount = (int)request.getSession().getAttribute("cartcount");
        List<Integer> gamesInCart = new ArrayList<>();
        gamesInCart = (List<Integer>)request.getSession().getAttribute("games");

        int pricefinal = 0;

        for (int i = 0; i < gamesInCart.size(); i++)
        {
            if (gamesInCart.get(i) == id)
            {
                gamesInCart.remove(i);

                cartCount--;

                session.setAttribute("games", gamesInCart);
                session.setAttribute("cartcount", cartCount);

                for (Integer k = 0; k < gamesInCart.size(); k++)
                {
                    //pricefinal += Integer.parseInt( prices.get( gamesInCart.get(k) ) );
                    pricefinal += shopRespository.getOne(gamesInCart.get(k)).getPrice();
                }

                return String.valueOf(pricefinal);
            }
        }

        return "";
    }

    @PostMapping("/add/{gameId}")
    public @ResponseBody String addPost(@PathVariable("gameId") int id, HttpServletRequest request, HttpSession session)
    {
        id += 1;

        int cartCount = (int)request.getSession().getAttribute("cartcount");
        List<Integer> gamesInCart = new ArrayList<>();
        gamesInCart = (List<Integer>)request.getSession().getAttribute("games");

        int pricefinal = 0;

        for (int i = 0; i < gamesInCart.size(); i++)
        {
            if (gamesInCart.get(i) == id)
            {
                gamesInCart.remove(i);

                cartCount--;

                session.setAttribute("games", gamesInCart);
                session.setAttribute("cartcount", cartCount);

                for (Integer k = 0; k < gamesInCart.size(); k++)
                {
                    //pricefinal += Integer.parseInt( prices.get( gamesInCart.get(k) ) );
                    pricefinal += shopRespository.getOne(k + 1).getPrice();
                }

                return "NULL " + Integer.toString(cartCount);
            }
        }

        gamesInCart.add(id);

        cartCount++;

        session.setAttribute("cartcount", cartCount);
        session.setAttribute("games", gamesInCart);

        return Integer.toString(cartCount);
    }


    @GetMapping("/getFreeGames")
    public @ResponseBody String  getFreeGames(Model model)
    {
        String freeGames = "";

        int count = 0;

        for (int i = 1; i <= shopRespository.count(); i++)
        {
            if (shopRespository.getOne(i).getPrice() == 0)
            {
                freeGames += " " + i;

                count++;
            }
        }

        String send = Integer.toString(count) + freeGames;

        return send;
    }


    @GetMapping("/getUnder100")
    public @ResponseBody String getGamesUnder100(Model model)
    {
        String freeGames = "";

        int count = 0;

        for (int i = 1; i <= shopRespository.count(); i++)
        {
            if (shopRespository.getOne(i).getPrice() < 1000)
            {
                freeGames += " " + i;

                count++;
            }
        }

        String send = Integer.toString(count) + freeGames;

        return send;
    }
}