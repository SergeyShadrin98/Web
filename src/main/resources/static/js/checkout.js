$(document).ready(function()
{
    var gameCount = $("#gameCount").text().slice(15, $("#gameCount").text().length);
    if ( gameCount == 0 )
    {
        $("#checkoutButton").hide();

        $("#gameCount").hide();

        $("#price").text("Ваша корзина пуста.");
    }
})


function deleteFromCart(clicked_id)
{
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function()
    {
       if (this.readyState == 4 && this.status == 200) // request finished and response is ready
       {
                $("#div-" + clicked_id).remove();

                var count = $("div.game").length;

                if (count == 0)
                {
                    $("#checkoutButton").hide();

                    $("#gameCount").hide();

                    $("#price").text("Ваша корзина пуста.");
                }
                else
                {
                    document.getElementById("gameCount").innerText = "В корзине игр: " + count;

                    document.getElementById("price").innerText = "Финальная цена: " + this.responseText;
                }

       }
    };

    var URL = location.href;
    URL = URL.substring(0, URL.length - 1);
    URL += "/erase/" + clicked_id;
    xhttp.open("POST", URL, true);
    xhttp.send();
}