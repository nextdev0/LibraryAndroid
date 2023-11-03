@file:Suppress(
  "unused",
  "SpellCheckingInspection",
  "MemberVisibilityCanBePrivate",
  "RedundantGetter",
)

package com.nextstory.util

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.PropertyChangeRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import com.nextstory.util.Json.Companion.jsonDecode
import com.nextstory.util.Json.Companion.jsonEncode
import java.lang.reflect.Type
import kotlin.properties.ReadOnlyProperty

/**
 * json 값 구성
 *
 * 예시:
 * ```
 * jsonOf(0) // '{ 0 }'
 * ```
 *
 * @param value 값
 * @return [Json]
 */
fun jsonOf(value: Any): Json {
  return Json(value)
}

/**
 * json object 구성
 *
 * 예시:
 * ```
 * jsonOf(
 *   "test" to 0,
 *   "test2" to "test!",
 * ) // '{ "test": 0, "test2": "test!" }'
 * ```
 *
 * @param pairs 요소 (가변 인자)
 * @return [Json]
 */
fun jsonOf(vararg pairs: Pair<String, Any?>): Json {
  val json = Json()
  json.isMap = true

  for (e in pairs) {
    json[e.first] = e.second
  }

  return json
}

/**
 * json array 구성
 *
 * 예시:
 * ```
 * jsonArrayOf(
 *   "test1",
 *   "test2",
 *   "test3",
 * ) // '[ "test1", "test2", "test3" ]'
 * ```
 *
 * @param list 요소 (가변 인자)
 * @return [Json]
 */
fun jsonArrayOf(vararg list: Any?): Json {
  val json = Json()
  json.isArray = true

  for (e in list) {
    json.add(e)
  }

  return json
}

/**
 * 상태 저장이 가능한 json 값 구성
 *
 * 예시:
 * ```
 * val json by savedStateJsonOf(0) // '{ 0 }'
 * ```
 *
 * @param value 값
 * @return [Json]
 *
 * @see jsonOf
 * @see androidx.activity.ComponentActivity
 * @see androidx.fragment.app.Fragment
 */
fun savedStateJsonOf(value: Any) = savedStateJsonOfImpl {
  Json(value)
}

/**
 * 상태 저장이 가능한 json object 구성
 *
 * 예시:
 * ```
 * val json by savedStateJsonOf(
 *   "test" to 0,
 *   "test2" to "test!",
 * ) // '{ "test": 0, "test2": "test!" }'
 * ```
 *
 * @param pairs 요소 (가변 인자)
 * @return [Json]
 */
fun savedStateJsonOf(vararg pairs: Pair<String, Any?>) = savedStateJsonOfImpl {
  val json = Json()
  json.isMap = true

  for (e in pairs) {
    json[e.first] = e.second
  }

  json
}

/**
 * 상태 저장이 가능한 json array 구성
 *
 * 예시:
 * ```
 * val json by savedStateJsonArrayOf(
 *   "test1",
 *   "test2",
 *   "test3",
 * ) // '[ "test1", "test2", "test3" ]'
 * ```
 *
 * @param list 요소 (가변 인자)
 * @return [Json]
 */
fun savedStateJsonArrayOf(vararg list: Any?) = savedStateJsonOfImpl {
  val json = Json()
  json.isArray = true

  for (e in list) {
    json.add(e)
  }

  json
}

private fun savedStateJsonOfImpl(
  block: () -> Json,
): ReadOnlyProperty<SavedStateRegistryOwner, Json> {
  var json: Json? = null
  return ReadOnlyProperty { savedStateRegistryOwner, property ->
    if (json == null) {
      val key = "saved_state:json:${property.name}"

      val bundle = savedStateRegistryOwner.savedStateRegistry.consumeRestoredStateForKey(key)
      json = if (bundle != null) {
        val savedState = bundle.getString(key)!!
        savedState.jsonDecode()
      } else {
        block()
      }

      savedStateRegistryOwner.savedStateRegistry.registerSavedStateProvider(key) {
        val outState = Bundle()
        outState.putString(key, json!!.jsonEncode())
        outState
      }
    }

    json!!
  }
}

class Json internal constructor(defaultValue: Any? = null) : Observable, Parcelable {
  private var callbacks: PropertyChangeRegistry? = null

  private val internalList = mutableListOf<Json>()
  private val internalMap = mutableMapOf<String, Json>()

  internal var isNull = false
  internal var isArray = false
  internal var isMap = false

  val list: List<Json> get() = internalList
  val map: Map<String, Json> get() = internalMap

  var value: Any? = defaultValue
    get() = field
    set(value) {
      if (isNull || isArray || isMap) {
        throw UnsupportedOperationException()
      }

      field = value
      notifyChange()
    }

  var boolValue: Boolean
    get() = when (value) {
      is Boolean -> value as Boolean
      is String -> (value as String).lowercase() == "true"
      is Number -> (value as Number).toInt() != 0
      else -> false
    }
    set(value) {
      this.value = value
    }

  var strValue: String
    get() = when (value) {
      is String -> value as String
      else -> (value == null).takeIfOrElse("", value.toString())
    }
    set(value) {
      this.value = value
    }

  var byteValue: Byte
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1, 0)
      is String -> (value as String).toByteOrNull() ?: 0
      is Number -> (value as Number).toByte()
      else -> 0
    }
    set(value) {
      this.value = value
    }

  var intValue: Int
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1, 0)
      is String -> (value as String).toIntOrNull() ?: 0
      is Number -> (value as Number).toInt()
      else -> 0
    }
    set(value) {
      this.value = value
    }

  var shortValue: Short
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1, 0)
      is String -> (value as String).toShortOrNull() ?: 0
      is Number -> (value as Number).toShort()
      else -> 0
    }
    set(value) {
      this.value = value
    }

  var longValue: Long
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1L, 0L)
      is String -> (value as String).toLongOrNull() ?: 0L
      is Number -> (value as Number).toLong()
      else -> 0L
    }
    set(value) {
      this.value = value
    }

  var floatValue: Float
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1f, 0f)
      is String -> (value as String).toFloatOrNull() ?: 0f
      is Number -> (value as Number).toFloat()
      else -> 0f
    }
    set(value) {
      this.value = value
    }

  var doubleValue: Double
    get() = when (value) {
      is Boolean -> (value as Boolean).takeIfOrElse(1.0, 0.0)
      is String -> (value as String).toDoubleOrNull() ?: 0.0
      is Number -> (value as Number).toDouble()
      else -> 0.0
    }
    set(value) {
      this.value = value
    }

  override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
    synchronized(this) {
      if (callbacks == null) {
        callbacks = PropertyChangeRegistry()
      }
    }
    callbacks!!.add(callback)
  }

  override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
    synchronized(this) {
      if (callbacks == null) {
        return
      }
    }
    callbacks!!.remove(callback)
  }

  fun notifyChange() {
    synchronized(this) {
      if (callbacks == null) {
        return
      }
    }
    callbacks!!.notifyCallbacks(this, 0, null)
  }

  override fun toString(): String {
    return if (isArray) {
      "$internalList"
    } else if (isMap) {
      "$internalMap"
    } else {
      "$value"
    }
  }

  operator fun get(index: Int): Json {
    if (value != null || isMap) {
      throw UnsupportedOperationException()
    }

    if (index < internalList.size) {
      return internalList[index]
    }

    return NULL
  }

  operator fun get(key: String): Json {
    if (isArray) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      return NULL
    }

    if (!internalMap.containsKey(key)) {
      internalMap[key] = Json()
    }

    return internalMap[key]!!
  }

  operator fun set(index: Int, value: Any?) {
    if (this.value != null || isMap) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      throw UnsupportedOperationException()
    }

    internalList[index] = when (value) {
      is Json -> {
        value
      }

      null -> {
        NULL
      }

      else -> {
        Json(value)
      }
    }

    notifyChange()
  }

  operator fun set(key: String, value: Any?) {
    if (this.value != null || isArray) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      throw UnsupportedOperationException()
    }

    internalMap[key] = when (value) {
      is Json -> {
        value
      }

      null -> {
        NULL
      }

      else -> {
        Json(value)
      }
    }

    notifyChange()
  }

  fun add(value: Any?) {
    if (this.value != null || isMap) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      throw UnsupportedOperationException()
    }

    internalList.add(
      when (value) {
        is Json -> {
          value
        }

        null -> {
          NULL
        }

        else -> {
          Json(value)
        }
      }
    )

    notifyChange()
  }

  fun addAll(list: Collection<Any?>) {
    if (value != null || isMap) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      throw UnsupportedOperationException()
    }

    for (e in list) {
      add(e)
    }
  }

  fun addAll(vararg list: Any?) {
    if (value != null || isMap) {
      throw UnsupportedOperationException()
    }

    if (isNull) {
      throw UnsupportedOperationException()
    }

    for (e in list) {
      add(e)
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    val json = jsonEncode()
    parcel.writeString(json)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object {
    internal val gson = GsonBuilder()
      .registerTypeAdapter(Json::class.java, TypeAdapter())
      .create()

    val NULL = Json().also { it.isNull = true }

    fun Json?.jsonEncode(): String {
      if (this == null) {
        return "null"
      }

      return if (isArray) {
        gson.toJson(internalList)
      } else if (isMap) {
        gson.toJson(internalMap)
      } else {
        gson.toJson(this)
      }
    }

    fun String?.jsonDecode(): Json {
      if (this == null || this == "null") {
        return NULL
      }
      return gson.fromJson(this, Json::class.java)
    }

    @JvmField
    val CREATOR = object : Parcelable.Creator<Json> {
      override fun createFromParcel(parcel: Parcel): Json {
        val json = parcel.readString()
        return json.jsonDecode()
      }

      override fun newArray(size: Int): Array<Json?> {
        return arrayOfNulls(size)
      }
    }
  }

  class TypeAdapter : JsonSerializer<Json>, JsonDeserializer<Json> {
    private val listType = TypeToken.getParameterized(
      ArrayList::class.java,
      Json::class.java,
    ).type

    private val mapType = TypeToken.getParameterized(
      LinkedHashMap::class.java,
      String::class.java,
      Json::class.java,
    ).type

    override fun serialize(
      src: Json,
      typeOfSrc: Type?,
      context: JsonSerializationContext
    ): JsonElement {
      return if (src.isArray) {
        context.serialize(src.internalList)
      } else if (src.isMap) {
        context.serialize(src.internalMap)
      } else {
        context.serialize(src.value)
      }
    }

    override fun deserialize(
      json: JsonElement,
      typeOfT: Type?,
      context: JsonDeserializationContext
    ): Json {
      return if (json.isJsonArray) {
        val newJson = Json()
        newJson.isArray = true

        val list = context.deserialize<ArrayList<Json>>(json, listType)
        newJson.addAll(list)

        newJson
      } else if (json.isJsonObject) {
        val newJson = Json()
        newJson.isMap = true

        val map = context.deserialize<LinkedHashMap<String, Json>>(json, mapType)
        for (e in map) {
          newJson[e.key] = e.value
        }

        newJson
      } else if (json.isJsonPrimitive) {
        val newJson = Json()

        val primitive = json.asJsonPrimitive
        newJson.value = if (primitive.isBoolean) {
          primitive.asBoolean
        } else if (primitive.isString) {
          primitive.asString
        } else if (primitive.isNumber) {
          primitive.asNumber
        } else {
          null
        }

        newJson
      } else {
        NULL
      }
    }
  }
}
