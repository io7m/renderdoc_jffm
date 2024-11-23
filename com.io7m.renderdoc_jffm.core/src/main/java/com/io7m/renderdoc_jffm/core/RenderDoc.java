/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.renderdoc_jffm.core;

import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.APIValidation;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.AllowFullscreen;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.AllowVSync;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureAllCmdLists;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureCallstacks;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.CaptureCallstacksOnlyActions;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.DebugOutputMute;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.DelayForDebugger;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.HookIntoChildren;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.RefAllResources;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.SoftMemoryLimit;
import com.io7m.renderdoc_jffm.core.RenderDocOptionType.VerifyBufferAccess;
import com.io7m.renderdoc_jffm.core.generated.RENDERDOC_API_1_6_0;
import com.io7m.renderdoc_jffm.core.generated.RenderDocEntrypoints;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetAPI;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetAPIVersion;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetCaptureFilePathTemplate;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetCaptureOptionF32;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetCaptureOptionU32;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_GetNumCaptures;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_SetCaptureFilePathTemplate;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_SetCaptureOptionF32;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_SetCaptureOptionU32;
import com.io7m.renderdoc_jffm.core.generated.pRENDERDOC_TriggerCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_API_Version_1_6_0;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_APIValidation;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_AllowFullscreen;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_AllowVSync;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_CaptureAllCmdLists;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_CaptureCallstacks;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_CaptureCallstacksOnlyActions;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_DebugOutputMute;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_DelayForDebugger;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_HookIntoChildren;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_RefAllResources;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_SoftMemoryLimit;
import static com.io7m.renderdoc_jffm.core.generated.renderdoc_app_h.eRENDERDOC_Option_VerifyBufferAccess;
import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

/**
 * The main RenderDoc implementation.
 */

public final class RenderDoc implements RenderDocType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RenderDoc.class);

  private final CloseableCollectionType<IOException> resources;
  private final TriggerCapture triggerCapture;
  private final SetCaptureOptionU32 setCaptureOptionU32;
  private final SetCaptureOptionF32 setCaptureOptionF32;
  private final GetCaptureOptionU32 getCaptureOptionU32;
  private final GetCaptureOptionF32 getCaptureOptionF32;
  private final SetCaptureFilePathTemplate setCaptureFilePathTemplate;
  private final GetCaptureFilePathTemplate getCaptureFilePathTemplate;
  private final GetNumCaptures getNumCaptures;
  private final AtomicBoolean closed;

  private RenderDoc(
    final CloseableCollectionType<IOException> inResources,
    final TriggerCapture inTriggerCapture,
    final SetCaptureOptionU32 inSetCaptureOptionU32,
    final SetCaptureOptionF32 inSetCaptureOptionF32,
    final GetCaptureOptionU32 inGetCaptureOptionU32,
    final GetCaptureOptionF32 inGetCaptureOptionF32,
    final SetCaptureFilePathTemplate inSetCaptureFilePathTemplate,
    final GetCaptureFilePathTemplate inGetCaptureFilePathTemplate,
    final GetNumCaptures inGetNumCaptures)
  {
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.triggerCapture =
      Objects.requireNonNull(inTriggerCapture, "triggerCapture");
    this.setCaptureOptionU32 =
      Objects.requireNonNull(inSetCaptureOptionU32, "setCaptureOptionU32");
    this.setCaptureOptionF32 =
      Objects.requireNonNull(inSetCaptureOptionF32, "setCaptureOptionF32");
    this.getCaptureOptionU32 =
      Objects.requireNonNull(inGetCaptureOptionU32, "getCaptureOptionU32");
    this.getCaptureOptionF32 =
      Objects.requireNonNull(inGetCaptureOptionF32, "getCaptureOptionF32");
    this.setCaptureFilePathTemplate =
      Objects.requireNonNull(
        inSetCaptureFilePathTemplate,
        "setCaptureFilePathTemplate"
      );
    this.getCaptureFilePathTemplate =
      Objects.requireNonNull(
        inGetCaptureFilePathTemplate,
        "getCaptureFilePathTemplate"
      );
    this.getNumCaptures =
      Objects.requireNonNull(inGetNumCaptures, "inGetNumCaptures");

    this.closed =
      new AtomicBoolean(false);
  }

  /**
   * Try to open RenderDoc.
   *
   * @return The RenderDoc API
   *
   * @throws IOException If RenderDoc cannot be loaded
   */

  public static RenderDocType open()
    throws IOException
  {
    final var resources =
      CloseableCollection.create(() -> {
        return new IOException(
          "One or more resources could not be closed."
        );
      });

    try {
      final var arena =
        resources.add(Arena.ofConfined());

      LOG.trace("Looking up renderdoc library…");
      final var library =
        RenderDocEntrypoints.symbolLookup();

      LOG.trace("Retrieving RENDERDOC_GetAPI symbol…");
      final var getAPI =
        library.find("RENDERDOC_GetAPI")
          .orElseThrow(() -> {
            return new IOException("No RENDERDOC_GetAPI symbol found.");
          });

      LOG.trace(
        "RENDERDOC_GetAPI: 0x{}",
        Long.toUnsignedString(getAPI.address(), 16)
      );

      /*
       * Allocate space for the struct that contains the API's function
       * pointers. Then pass a pointer to the address of the struct to
       * the API. Essentially:
       *
       * struct RENDERDOC_API_1_6_0 x = malloc(…);
       * RENDERDOC_GetAPI(…, &x);
       */

      final var layoutPtrToAPI =
        ADDRESS.withTargetLayout(RENDERDOC_API_1_6_0.layout());
      final var layoutPtrToPtrAPI =
        ADDRESS.withTargetLayout(layoutPtrToAPI);

      final var apiPtrPtr =
        arena.allocate(layoutPtrToPtrAPI);

      LOG.trace("Invoking RENDERDOC_GetAPI…");
      final var r =
        pRENDERDOC_GetAPI.invoke(
          getAPI,
          eRENDERDOC_API_Version_1_6_0(),
          apiPtrPtr
        );

      if (r != 1) {
        throw new IOException(
          "RENDERDOC_GetAPI returned error code %s".formatted(r)
        );
      }

      /*
       * Dereference the pointer-to-pointer, yielding a pointer to
       * a RENDERDOC_API_1_6_0 struct. It's then necessary to reinterpret
       * that pointer in order to provide the FFM API with a memory segment
       * that's of the correct size (the size of the struct).
       */

      final var apiAddress =
        apiPtrPtr.get(JAVA_LONG, 0L);

      LOG.trace(
        "RENDERDOC_API_1_6_0: 0x{}",
        Long.toUnsignedString(apiAddress, 16)
      );

      final var apiPtr =
        RENDERDOC_API_1_6_0.reinterpret(
          MemorySegment.ofAddress(apiAddress),
          arena,
          null
        );

      LOG.trace(
        "RENDERDOC_API_1_6_0: size {}",
        Long.toUnsignedString(apiPtr.byteSize())
      );

      /*
       * Look up and call GetAPIVersion as a sanity check.
       */

      final var getAPIVersionPtr =
        RENDERDOC_API_1_6_0.GetAPIVersion(apiPtr);

      LOG.trace(
        "RENDERDOC_GetAPIVersion: 0x{}",
        Long.toUnsignedString(getAPIVersionPtr.address(), 16)
      );

      final var majorBuf =
        arena.allocate(JAVA_INT);
      final var minorBuf =
        arena.allocate(JAVA_INT);
      final var patchBuf =
        arena.allocate(JAVA_INT);

      LOG.trace("Invoking RENDERDOC_GetAPIVersion…");
      pRENDERDOC_GetAPIVersion.invoke(
        getAPIVersionPtr,
        MemorySegment.ofAddress(majorBuf.address()),
        MemorySegment.ofAddress(minorBuf.address()),
        MemorySegment.ofAddress(patchBuf.address())
      );

      final var major = majorBuf.get(JAVA_INT, 0);
      final var minor = minorBuf.get(JAVA_INT, 0);
      final var patch = patchBuf.get(JAVA_INT, 0);
      LOG.trace("Version: {}.{}.{}", major, minor, patch);

      if (major != 1 || minor != 6 || patch != 0) {
        throw new IOException(
          "Incompatible RenderDoc API (Expected 1.6.0, received %d.%d.%d)"
            .formatted(major, minor, patch)
        );
      }

      /*
       * Obtain the rest of the API.
       */

      final var triggerCapturePtr =
        RENDERDOC_API_1_6_0.TriggerCapture(apiPtr);
      final var setCaptureOptionU32Ptr =
        RENDERDOC_API_1_6_0.SetCaptureOptionU32(apiPtr);
      final var setCaptureOptionF32Ptr =
        RENDERDOC_API_1_6_0.SetCaptureOptionF32(apiPtr);
      final var getCaptureOptionU32Ptr =
        RENDERDOC_API_1_6_0.GetCaptureOptionU32(apiPtr);
      final var getCaptureOptionF32Ptr =
        RENDERDOC_API_1_6_0.GetCaptureOptionF32(apiPtr);
      final var setCaptureFilePathTemplatePtr =
        RENDERDOC_API_1_6_0.SetCaptureFilePathTemplate(apiPtr);
      final var getCaptureFilePathTemplatePtr =
        RENDERDOC_API_1_6_0.GetCaptureFilePathTemplate(apiPtr);
      final var getNumCapturesPtr =
        RENDERDOC_API_1_6_0.GetNumCaptures(apiPtr);

      LOG.trace(
        "RENDERDOC_TriggerCapture: 0x{}",
        Long.toUnsignedString(triggerCapturePtr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_SetCaptureOptionU32: 0x{}",
        Long.toUnsignedString(setCaptureOptionU32Ptr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_SetCaptureOptionF32: 0x{}",
        Long.toUnsignedString(setCaptureOptionF32Ptr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_GetCaptureOptionU32: 0x{}",
        Long.toUnsignedString(getCaptureOptionU32Ptr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_GetCaptureOptionF32: 0x{}",
        Long.toUnsignedString(getCaptureOptionF32Ptr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_SetCaptureFilePathTemplate: 0x{}",
        Long.toUnsignedString(setCaptureFilePathTemplatePtr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_GetCaptureFilePathTemplate: 0x{}",
        Long.toUnsignedString(getCaptureFilePathTemplatePtr.address(), 16)
      );
      LOG.trace(
        "RENDERDOC_GetNumCaptures: 0x{}",
        Long.toUnsignedString(getNumCapturesPtr.address(), 16)
      );

      final var triggerCapture =
        new TriggerCapture(triggerCapturePtr);
      final var setCaptureOptionU32 =
        new SetCaptureOptionU32(setCaptureOptionU32Ptr);
      final var setCaptureOptionF32 =
        new SetCaptureOptionF32(setCaptureOptionF32Ptr);
      final var getCaptureOptionU32 =
        new GetCaptureOptionU32(getCaptureOptionU32Ptr);
      final var getCaptureOptionF32 =
        new GetCaptureOptionF32(getCaptureOptionF32Ptr);
      final var setCaptureFilePathTemplate =
        new SetCaptureFilePathTemplate(arena, setCaptureFilePathTemplatePtr);
      final var getCaptureFilePathTemplate =
        new GetCaptureFilePathTemplate(arena, getCaptureFilePathTemplatePtr);
      final var getNumCaptures =
        new GetNumCaptures(getNumCapturesPtr);

      return new RenderDoc(
        resources,
        triggerCapture,
        setCaptureOptionU32,
        setCaptureOptionF32,
        getCaptureOptionU32,
        getCaptureOptionF32,
        setCaptureFilePathTemplate,
        getCaptureFilePathTemplate,
        getNumCaptures
      );
    } catch (final Throwable e) {
      resources.close();
      throw new IOException(e);
    }
  }

  private record SetCaptureFilePathTemplate(
    Arena arena,
    MemorySegment address)
  {
    void call(
      final Path file)
    {
      pRENDERDOC_SetCaptureFilePathTemplate.invoke(
        this.address,
        this.arena.allocateFrom(file.toString())
      );
    }
  }

  private record GetCaptureFilePathTemplate(
    Arena arena,
    MemorySegment address)
  {
    Optional<Path> call()
    {
      final var r =
        pRENDERDOC_GetCaptureFilePathTemplate.invoke(this.address);

      return Optional.ofNullable(r.getString(0L))
        .map(Paths::get);
    }
  }

  private record TriggerCapture(
    MemorySegment address)
  {
    void call()
    {
      pRENDERDOC_TriggerCapture.invoke(this.address);
    }
  }

  private record SetCaptureOptionU32(
    MemorySegment address)
  {
    void call(
      final int option,
      final int value)
    {
      pRENDERDOC_SetCaptureOptionU32.invoke(
        this.address,
        option,
        value
      );
    }
  }

  private record SetCaptureOptionF32(
    MemorySegment address)
  {
    void call(
      final int option,
      final float value)
    {
      pRENDERDOC_SetCaptureOptionF32.invoke(
        this.address,
        option,
        value
      );
    }
  }

  private record GetCaptureOptionU32(
    MemorySegment address)
  {
    int call(
      final int option)
    {
      return pRENDERDOC_GetCaptureOptionU32.invoke(
        this.address,
        option
      );
    }
  }

  private record GetCaptureOptionF32(
    MemorySegment address)
  {
    float call(
      final int option)
    {
      return pRENDERDOC_GetCaptureOptionF32.invoke(
        this.address,
        option
      );
    }
  }

  private record GetNumCaptures(
    MemorySegment address)
  {
    long call()
    {
      return pRENDERDOC_GetNumCaptures.invoke(
        this.address
      );
    }
  }

  @Override
  public void triggerCapture()
  {
    this.checkNotClosed();
    LOG.trace("Triggering capture…");
    this.triggerCapture.call();
  }

  @Override
  public long numberOfCaptures()
  {
    this.checkNotClosed();
    return this.getNumCaptures.call();
  }

  @Override
  public Optional<Path> captureFilePathTemplate()
  {
    this.checkNotClosed();
    return this.getCaptureFilePathTemplate.call();
  }

  @Override
  public void setCaptureFilePathTemplate(
    final Path file)
  {
    Objects.requireNonNull(file, "file");
    this.checkNotClosed();
    this.setCaptureFilePathTemplate.call(file);
  }

  @Override
  public void setOption(
    final RenderDocOptionType option)
  {
    Objects.requireNonNull(option, "option");

    switch (option) {
      case final APIValidation o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_APIValidation(),
          o.enabled() ? 1 : 0
        );
      }
      case final AllowFullscreen o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_AllowFullscreen(),
          o.allow() ? 1 : 0
        );
      }
      case final AllowVSync o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_AllowVSync(),
          o.allow() ? 1 : 0
        );
      }
      case final CaptureAllCmdLists o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_CaptureAllCmdLists(),
          o.enabled() ? 1 : 0
        );
      }
      case final CaptureCallstacks o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_CaptureCallstacks(),
          o.enabled() ? 1 : 0
        );
      }
      case final CaptureCallstacksOnlyActions o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_CaptureCallstacksOnlyActions(),
          o.enabled() ? 1 : 0
        );
      }
      case final DebugOutputMute o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_DebugOutputMute(),
          o.enabled() ? 1 : 0
        );
      }
      case final DelayForDebugger o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_DelayForDebugger(),
          o.seconds()
        );
      }
      case final HookIntoChildren o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_HookIntoChildren(),
          o.enabled() ? 1 : 0
        );
      }
      case final RefAllResources o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_RefAllResources(),
          o.enabled() ? 1 : 0
        );
      }
      case final SoftMemoryLimit o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_SoftMemoryLimit(),
          o.megabytes()
        );
      }
      case final VerifyBufferAccess o -> {
        this.setCaptureOptionU32.call(
          eRENDERDOC_Option_VerifyBufferAccess(),
          o.enabled() ? 1 : 0
        );
      }
    }
  }

  @Override
  public <T extends RenderDocOptionType> T option(
    final Class<T> option)
  {
    Objects.requireNonNull(option, "option");

    if (Objects.equals(option, APIValidation.class)) {
      return (T) new APIValidation(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_APIValidation()) == 1
      );
    }
    if (Objects.equals(option, AllowFullscreen.class)) {
      return (T) new AllowFullscreen(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_AllowFullscreen()) == 1
      );
    }
    if (Objects.equals(option, AllowVSync.class)) {
      return (T) new AllowFullscreen(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_AllowVSync()) == 1
      );
    }
    if (Objects.equals(option, CaptureAllCmdLists.class)) {
      return (T) new CaptureAllCmdLists(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_CaptureAllCmdLists()) == 1
      );
    }
    if (Objects.equals(option, CaptureCallstacks.class)) {
      return (T) new CaptureCallstacks(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_CaptureCallstacks()) == 1
      );
    }
    if (Objects.equals(option, CaptureCallstacksOnlyActions.class)) {
      return (T) new CaptureCallstacksOnlyActions(
        this.getCaptureOptionU32.call(
          eRENDERDOC_Option_CaptureCallstacksOnlyActions()) == 1
      );
    }
    if (Objects.equals(option, DebugOutputMute.class)) {
      return (T) new DebugOutputMute(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_DebugOutputMute()) == 1
      );
    }
    if (Objects.equals(option, DelayForDebugger.class)) {
      return (T) new DelayForDebugger(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_DelayForDebugger())
      );
    }
    if (Objects.equals(option, HookIntoChildren.class)) {
      return (T) new HookIntoChildren(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_HookIntoChildren()) == 1
      );
    }
    if (Objects.equals(option, RefAllResources.class)) {
      return (T) new RefAllResources(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_RefAllResources()) == 1
      );
    }
    if (Objects.equals(option, SoftMemoryLimit.class)) {
      return (T) new SoftMemoryLimit(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_SoftMemoryLimit())
      );
    }
    if (Objects.equals(option, VerifyBufferAccess.class)) {
      return (T) new VerifyBufferAccess(
        this.getCaptureOptionU32.call(eRENDERDOC_Option_VerifyBufferAccess()) == 1
      );
    }

    throw new IllegalArgumentException(
      "Unrecognized option class: %s".formatted(option)
    );
  }

  private void checkNotClosed()
  {
    if (this.closed.get()) {
      throw new IllegalStateException("RenderDoc is closed.");
    }
  }

  @Override
  public void close()
    throws IOException
  {
    if (this.closed.compareAndSet(false, true)) {
      this.resources.close();
    }
  }
}
