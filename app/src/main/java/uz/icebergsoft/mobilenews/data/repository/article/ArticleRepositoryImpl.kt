package uz.icebergsoft.mobilenews.data.repository.article

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import uz.icebergsoft.mobilenews.data.datasource.database.dao.article.ArticleEntityDao
import uz.icebergsoft.mobilenews.data.datasource.rest.service.ArticleRestService
import uz.icebergsoft.mobilenews.data.mapper.entityToArticle
import uz.icebergsoft.mobilenews.data.mapper.responseToEntity
import uz.icebergsoft.mobilenews.domain.data.entity.article.Article
import uz.icebergsoft.mobilenews.domain.data.entity.article.ArticleListWrapper
import uz.icebergsoft.mobilenews.domain.data.repository.article.ArticleRepository
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

internal class ArticleRepositoryImpl @Inject constructor(
    private val articleEntityDao: ArticleEntityDao,
    private val articleRestService: ArticleRestService
) : ArticleRepository {

    override fun getArticle(articleId: String): Flow<Article> {
        return articleEntityDao.getArticleEntityById(articleId)
            .map { it.entityToArticle() }
    }

    @FlowPreview
    override fun getArticles(): Flow<ArticleListWrapper> {
        return articleRestService.getBreakingArticles()
            .onEach { it ->
                it.articles.forEach {
                    articleEntityDao.updateArticle(it.responseToEntity())
                }
            }
            .map { it -> it.articles.map { it.url } }
            .catch {
                if (it is ConnectException || it is UnknownHostException) emit(listOf())
                else throw it
            }
            .flatMapConcat { postUrls ->
                when {
                    postUrls.isNotEmpty() ->
                        articleEntityDao.getArticleEntitiesByUrl(postUrls.toTypedArray())
                    else -> articleEntityDao.getArticleEntities()
                }
                    .map { list -> list.map { it.entityToArticle() } }
                    .map { ArticleListWrapper(it, postUrls.isEmpty()) }
            }
    }

    @FlowPreview
    override fun getBreakingNewsArticles(): Flow<ArticleListWrapper> {
        return articleRestService.getBreakingArticles()
            .onEach { it ->
                it.articles.forEach {
                    articleEntityDao.updateArticle(it.responseToEntity())
                }
            }
            .map { it -> it.articles.map { it.url } }
            .catch {
                if (it is ConnectException || it is UnknownHostException) emit(listOf())
                else throw it
            }
            .flatMapConcat { postUrls ->
                when {
                    postUrls.isNotEmpty() ->
                        articleEntityDao.getArticleEntitiesByUrl(postUrls.toTypedArray())
                    else -> articleEntityDao.getArticleEntities()
                }
                    .map { list -> list.map { it.entityToArticle() } }
                    .map { ArticleListWrapper(it, postUrls.isEmpty()) }
            }
    }

    @FlowPreview
    override fun getTopArticles(): Flow<ArticleListWrapper> {
        return articleRestService.getTopArticles()
            .onEach { it ->
                it.articles.forEach {
                    articleEntityDao.updateArticle(it.responseToEntity())
                }
            }
            .map { it -> it.articles.map { it.url } }
            .catch {
                if (it is ConnectException || it is UnknownHostException) emit(listOf())
                else throw it
            }
            .flatMapConcat { postUrls ->
                when {
                    postUrls.isNotEmpty() ->
                        articleEntityDao.getArticleEntitiesByUrl(postUrls.toTypedArray())
                    else -> articleEntityDao.getArticleEntities()
                }
                    .map { list -> list.map { it.entityToArticle() } }
                    .map { ArticleListWrapper(it, postUrls.isEmpty()) }
            }
    }

    @FlowPreview
    override fun getRecommendedArticles(): Flow<ArticleListWrapper> {
        return articleRestService.getRecommendationArticles()
            .onEach { it ->
                it.articles.forEach {
                    articleEntityDao.updateArticle(it.responseToEntity())
                }
            }
            .map { it -> it.articles.map { it.url } }
            .catch {
                if (it is ConnectException || it is UnknownHostException) emit(listOf())
                else throw it
            }
            .flatMapConcat { postUrls ->
                when {
                    postUrls.isNotEmpty() ->
                        articleEntityDao.getArticleEntitiesByUrl(postUrls.toTypedArray())
                    else -> articleEntityDao.getArticleEntities()
                }
                    .map { list -> list.map { it.entityToArticle() } }
                    .map { ArticleListWrapper(it, postUrls.isEmpty()) }
            }
    }

    @FlowPreview
    override fun getReadLaterArticles(): Flow<ArticleListWrapper> {
        return flow { emit(articleEntityDao.getArticleEntitiesByBookmark(true).first()) }
            .map { list -> list.map { it.entityToArticle() } }
            .map { ArticleListWrapper(it, true) }
    }

    override fun updateBookmark(articleId: String, isBookmarked: Boolean): Flow<Unit> {
        return flow { emit(articleEntityDao.updateBookmark(articleId, isBookmarked)) }
    }
}