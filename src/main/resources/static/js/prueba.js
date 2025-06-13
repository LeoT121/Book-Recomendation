document.addEventListener("DOMContentLoaded", function() {
    const favoritos = document.querySelectorAll('div[name="authors"]');
    let unique = new Set();

    Array.from(favoritos).map(div => {
        let autor = div.getAttribute("data-idauthor");
        let uniqueAuthors = [...new Set(
            autor
            .split(",")
            .map(author => author.trim())
            .filter(author => author !== "")
            )];
        for(let val of uniqueAuthors)
            unique.add(val);
    });
    for(let value of unique){
        console.log(value);
    }
});