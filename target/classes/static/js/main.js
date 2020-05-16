function openModal()
{
    var modal = document.getElementById("myModal");
    modal.style.display = "block";

    var span = document.getElementsByClassName("close")[0];
    span.onclick = function() {
      modal.style.display = "none";
    }

    window.onclick = function(event) {
      if (event.target == modal) {
        modal.style.display = "none";
      }
    }
}

function setSpot(link)
{
    var modal = document.getElementById("myModal");
    modal.style.display = "none";

    document.getElementById("spot").innerText = link.name + '.';

    var URL = location.href;
    if (URL.charAt( URL.length - 1 ) == "n")
    {
        URL = URL.substring( 0, URL.length - 9 );
    }
    URL += "setSpot/" + link.name;

    window.location.href = URL;
}


function addToCart(clicked_id)
{
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function()
    {
       if (this.readyState == 4 && this.status == 200) // request finished and response is ready
       {
            if (this.responseText.substring(0, 4) == "NULL")
            {
                $(this).text('Добавить в корзину');

                document.getElementById("id_cartcount").innerText = this.responseText.slice(5);

                $('#submitButton' + clicked_id).text('Добавить в корзину');
            }
            else
            {
                document.getElementById("id_cartcount").innerText = this.responseText;
                $('#submitButton' + clicked_id).text('Убрать из корзины');
            }
       }
    };

    var URL = location.href;
    if (URL.charAt( URL.length - 1 ) == "e")
    {
        URL = URL.substring( 0, URL.length - 13 );
    }
    URL += "add/" + clicked_id;
    xhttp.open("POST", URL, true);
    xhttp.send();
}

function SortByPrice(a, b)
{
  if ( parseInt(document.getElementById($(a).get(0).id + '-p').innerText) < parseInt(document.getElementById($(b).get(0).id + '-p').innerText) )
  {
      return -1;
  }
  if ( parseInt(document.getElementById($(a).get(0).id + '-p').innerText) > parseInt(document.getElementById($(b).get(0).id + '-p').innerText) )
  {
      return 1;
  }
  return 0;
}

function filtersort()
{
    var checkedValue = $('#filtersort:checked').val();

    if (checkedValue == "on")
    {
        var checkedValue1 = $('#filterFree:checked').val();
        var checkedValue2 = $('#filter2:checked').val();
        if (checkedValue1 == "on")
        {
            $('#filterFree').prop('checked', false);
            $("div.game").show();
        }

        if (checkedValue2 == "on")
        {
            $('#filter2').prop('checked', false);
            $("div.game").show();
        }


        var divs = new Array();
        var divObjects = new Array();

        $('div.game').each(function(i, ele)
        {
            divs.push(ele.id);
            divObjects.push($('#'+ele.id).clone());
        })

        sessionStorage.setItem("originalDivs", JSON.stringify(divs));

        divObjects.sort(SortByPrice);

        $('div.game').each(function(i, ele)
                {
                    $('#'+ele.id).remove();
                })

        for (let i = 0; i < divObjects.length; i++)
        {
            $(document.body).append(divObjects[i]);
            //$(divObjects.join('')).appendTo('body');
        }
    }
    else
    {
        var divs = JSON.parse(sessionStorage.getItem("originalDivs"));

        var alldivs = new Array();

        $('div.game').each(function(i, ele)
                {
                    alldivs.push($('#'+ele.id).clone());
                })

        $('div.game').each(function(i, ele)
                {
                    $('#'+ele.id).remove();
                })

        for (let i = 0; i < divs .length; i++)
        {
            for (let j = 0; j < alldivs.length; j++)
            {
                if ($(alldivs[j]).get(0).id == divs[i])
                {
                    $(document.body).append(alldivs[j]);
                    break;
                }
            }
        }
    }
}

function filterFree1()
{
    var checkedValue = $('#filterFree:checked').val();

    if(checkedValue == "on")
    {
        var checkedValue1 = $('#filter2:checked').val();
        var checkedValueSort = $('#filtersort:checked').val();
        if (checkedValue1 == "on")
        {
            $('#filter2').prop('checked', false);
            $("div.game").show();
        }

        if (checkedValueSort == "on")
        {
            $('#filtersort').prop('checked', false);
            filtersort();
        }

        var xhttp = new XMLHttpRequest();

        xhttp.onreadystatechange = function()
        {
           if (this.readyState == 4 && this.status == 200) // request finished and response is ready
           {
                $("div.game").hide();

                var answer = this.responseText;
                var countS = "";
                var ipos = 0;
                for (let i = 0; i < answer.length; i++)
                {
                    if (answer.charAt(i) == " ")
                    {
                        ipos = i + 1;
                        break;
                    }

                    countS += answer.charAt(i);
                }

                var iCount = parseInt(answer);



                for (let i = 0; i < iCount; i++)
                {
                    var gameId = "";

                    for (let j = ipos; j < answer.length; j++)
                    {
                        if (answer.charAt(j) == " ")
                        {
                            ipos = j + 1;
                            break;
                        }

                        gameId += answer.charAt(j);
                    }

                    gameId -= 1;

                    $("div.game").each(function( index  )
                    {
                        if ( $(this).attr('id') == "game-" + gameId )
                        {
                            $("#" + $(this).attr('id')).show();
                        }
                    })
                }
           }
        };

        var URL = location.href;
        URL += "getFreeGames";
        xhttp.open("GET", URL, true);
        xhttp.send();
    }
    else
    {
        $("div.game").show();
    }
}


function filterFree2()
{
    var checkedValue = $('#filter2:checked').val();

    if(checkedValue == "on")
    {
        var checkedValue1 = $('#filterFree:checked').val();
        var checkedValueSort = $('#filtersort:checked').val();
        if (checkedValue1 == "on")
        {
            $('#filterFree').prop('checked', false);
            $("div.game").show();
        }

        if (checkedValueSort == "on")
        {
            $('#filtersort').prop('checked', false);
            filtersort();
        }

        var xhttp = new XMLHttpRequest();

        xhttp.onreadystatechange = function()
        {
           if (this.readyState == 4 && this.status == 200) // request finished and response is ready
           {
                $("div.game").hide();

                var answer = this.responseText;
                var countS = "";
                var ipos = 0;
                for (let i = 0; i < answer.length; i++)
                {
                    if (answer.charAt(i) == " ")
                    {
                        ipos = i + 1;
                        break;
                    }

                    countS += answer.charAt(i);
                }

                var iCount = parseInt(answer);



                for (let i = 0; i < iCount; i++)
                {
                    var gameId = "";

                    for (let j = ipos; j < answer.length; j++)
                    {
                        if (answer.charAt(j) == " ")
                        {
                            ipos = j + 1;
                            break;
                        }

                        gameId += answer.charAt(j);
                    }

                    gameId -= 1;

                    $("div.game").each(function( index  )
                    {
                        if ( $(this).attr('id') == "game-" + gameId )
                        {
                            $("#" + $(this).attr('id')).show();
                        }
                    })
                }
           }
        };

        var URL = location.href;
        URL += "getUnder100";
        xhttp.open("GET", URL, true);
        xhttp.send();
    }
    else
    {
        $("div.game").show();
    }
}