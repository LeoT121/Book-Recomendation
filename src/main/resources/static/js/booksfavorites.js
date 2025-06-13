document.addEventListener('DOMContentLoaded', function() {
    const bookGrid = document.getElementById('bookGrid');
    const resultsSection = document.getElementById('resultsSection');
    const favoritos = document.querySelectorAll('div[name="authors"]');
    let unique = new Set();
    let favoritesCache = {};

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
        performSearch(1, value);
    }
    
    function performSearch(page, author) {
        
        if (author == "Autor Desconocido")
            return;
        
        // Mostrar loader
        resultsSection.style.display = 'none';
        
        // Construir la URL de búsqueda
        let searchQuery = [];
        searchQuery.push(`author=${encodeURIComponent(author)}`);
        
        // Si hay título, autor o categoría específicos, usamos los parámetros correspondientes
        // Si no, usamos un término de búsqueda general
        let apiUrl;
        if (searchQuery.length > 0) {
            apiUrl = `https://openlibrary.org/search.json?${searchQuery.join('&')}&page=${page}`;
        } else {
            // Búsqueda general si no hay criterios específicos
            apiUrl = `https://openlibrary.org/search.json?q=${encodeURIComponent(title || author || '')}`;
        }
        
        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta de la API');
                }
                return response.json();
            })
            .then(data => {
                // Ocultar loader
                
                if (data.docs && data.docs.length > 0) {
                    // Guardar todos los libros y la página actual
                    allBooks = data.docs.slice(0, 3);
                    currentPage = page;
                    totalResults = data.numFound;
                    checkFavoritesStatus(allBooks, author);
                    // Mostrar sección de resultados
                    resultsSection.style.display = 'block';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Ocurrió un error al buscar los libros. Por favor, intenta de nuevo.');
            });
    }
    
    function checkFavoritesStatus(books, author) {
        // Crear un array de promesas para verificar todos los libros
        const keyPromises = books.map(book => {
            const key = book.key;
            
            // Si ya tenemos el estado en caché, no necesitamos consultarlo de nuevo
            if (favoritesCache[key] !== undefined) {
                return Promise.resolve();
            }
            
            return fetch(`/api/libros/favoritos/verificar/${key}`)
                .then(response => response.json())
                .then(data => {
                    favoritesCache[key] = data.esFavorito;
                })
                .catch(error => {
                    console.error('Error al verificar favorito:', error);
                    favoritesCache[key] = false;
                });
        });
        
        // Cuando todas las verificaciones terminen, mostrar los libros
        Promise.all(keyPromises).then(() => {
            displayBooks(books, author);
        });
    }
    
    function displayBooks(books, author) {
        // Limpiar el contenedor de libros
        // Mostrar los libros
        books.forEach(book => {
            // Crear elemento de tarjeta para el libro
            const bookCard = document.createElement('div');
            bookCard.className = 'book-card';
            
            // Obtener la imagen de la portada si está disponible
            let coverUrl = '/images/placeholder-image.jpg';
            if (book.cover_i) {
                coverUrl = `https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg`;
            }
            
            // Obtener el año de publicación si está disponible
            let publishYear = book.first_publish_year || (book.publish_year ? book.publish_year[0] : 'Desconocido');
            
            // Obtener autores si están disponibles
            let authors = book.author_name ? book.author_name.join(', ') : 'Autor desconocido';
            
            
            // Verificar si el libro ya está en favoritos
            const isFavorite = favoritesCache[book.key] || false;
            
            // Configurar el botón de favoritos según el estado
            const favoriteButtonClass = isFavorite ? 'favorite-button added' : 'favorite-button';
            const favoriteButtonText = isFavorite ? 'En favoritos' : 'Agregar a favoritos';
            
            // Estructura HTML de la tarjeta
            bookCard.innerHTML = `
                <h3>Recomendaciones del autor(a) ${author}<h3>
                <div class="book-cover">
                    <img src="${coverUrl}" alt="Portada de ${book.title}" onerror="this.src='/api/placeholder/250/300'">
                </div>
                <div class="book-info">
                    <h3 class="book-title">${book.title}</h3>
                    <p class="book-author">${authors}</p>
                    <p class="book-year">${publishYear}</p>
                    <div class="book-actions">
                        <button class="book-button details-button" data-key="${book.key}">Ver detalles</button>
                        <button class="book-button ${favoriteButtonClass}" 
                                data-key="${book.key}" 
                                data-title="${book.title}" 
                                data-author="${authors}" 
                                data-cover="${coverUrl}"
                                onclick="toggleFavorite(this)">
                            ${favoriteButtonText}
                        </button>
                    </div>
                </div>
            `;
            
            // Agregar evento para el botón de detalles
            bookCard.querySelector('.details-button').addEventListener('click', function() {
                const key = this.getAttribute('data-key');
                window.open(`https://openlibrary.org${key}`, '_blank');
            });
            
            // Agregar la tarjeta al grid
            bookGrid.appendChild(bookCard);
        });
    }
});

// Función para alternar el estado de favorito
function toggleFavorite(button) {
    const libroId = button.getAttribute('data-key');
    const titulo = button.getAttribute('data-title');
    const autor = button.getAttribute('data-author');
    const imagenUrl = button.getAttribute('data-cover');
    
    if (button.classList.contains('added')) {
        // Ya está en favoritos, hay que quitarlo
        fetch(`/api/libros/favoritos/eliminar/${libroId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.mensaje) {
                button.classList.remove('added');
                button.textContent = 'Agregar a favoritos';
            } else if (data.error) {
                showToast(data.error, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    } else {
        // No está en favoritos, hay que agregarlo
        const formData = new FormData();
        formData.append('libroId', libroId);
        formData.append('titulo', titulo);
        formData.append('autor', autor);
        formData.append('imagenUrl', imagenUrl);
        
        fetch('/api/libros/favoritos/agregar', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.favorito) {
                button.classList.add('added');
                button.textContent = 'En favoritos';
                alert('Libro agregado a favoritos');
                window.location.reload();
            } else if (data.error) {
                showToast(data.error, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Error al agregar a favoritos");
        });
    }
}