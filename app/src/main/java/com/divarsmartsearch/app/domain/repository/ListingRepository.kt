package com.divarsmartsearch.app.domain.repository

import com.divarsmartsearch.app.domain.model.HistoryTab
import com.divarsmartsearch.app.domain.model.Listing
import com.divarsmartsearch.app.domain.model.SellerReport
import com.divarsmartsearch.app.util.AppResult
import kotlinx.coroutines.flow.Flow

interface ListingRepository {
    suspend fun getVisibleListings(searchId: Int? = null): AppResult<List<Listing>>

    /**
     * Live view of the visible listings, straight from the database. The
     * background scanner inserts new listings independently of any screen
     * being open, so the Results screen must observe this Flow (not just
     * fetch once) or newly ingested listings never appear until the
     * ViewModel happens to be recreated.
     */
    fun observeVisibleListings(searchId: Int? = null): Flow<List<Listing>>
    suspend fun getHistory(tab: HistoryTab): AppResult<List<Listing>>
    suspend fun markSeen(listingId: Int): AppResult<Unit>
    suspend fun saveListing(listingId: Int): AppResult<Unit>
    suspend fun rejectListing(listingId: Int): AppResult<Unit>

    /** Every stored listing (any search) that shares [phoneNumber], aggregated for display. */
    suspend fun getSellerReport(phoneNumber: String): AppResult<SellerReport>

    /**
     * Re-runs the filter pipeline against every listing currently showing
     * in the results list, using whatever keyword filters / owner-detection
     * settings exist *right now* — not whatever they were when each
     * listing was first ingested. This is what lets a keyword filter (or
     * threshold change) the person just added actually take effect on
     * listings that are already sitting in the list, instead of only
     * affecting listings scanned after the change.
     *
     * Deliberately scoped to isVisible=true listings only: anything the
     * person already saved or rejected keeps that outcome regardless of
     * filter changes, since that was a manual decision, not a filter one.
     */
    suspend fun reapplyFilters(): AppResult<Unit>
}
